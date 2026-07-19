#!/usr/bin/env bash
# Run the test suite with a quiet live progress line and a summary at the end.
#
# Usage:
#   ./scripts/run-tests.sh smoke        # default — AI stubs, no real OpenAI calls
#   ./scripts/run-tests.sh integration  # real OpenAI API (requires .env.test)

set -euo pipefail

MODE="${1:-smoke}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
REPORTS_DIR="$ROOT/target/surefire-reports"
LOG_FILE="$ROOT/target/test-run.log"
PARSER="$ROOT/scripts/surefire-report-parser.py"

cd "$ROOT"

case "$MODE" in
  smoke)
    LABEL="Smoke tests"
    ;;
  integration)
    LABEL="Integration tests"
    if [[ ! -f "$ROOT/.env.test" ]] && [[ -z "${OPEN_AI_KEY:-}" ]]; then
      echo "ERROR: integration mode requires .env.test or OPEN_AI_KEY in the environment" >&2
      exit 1
    fi
    ;;
  *)
    echo "Usage: $0 [smoke|integration]" >&2
    exit 2
    ;;
esac

set -a
if [[ -f "$ROOT/.env.test" ]]; then
  # shellcheck disable=SC1091
  source "$ROOT/.env.test"
fi
set +a

if [[ "$MODE" == "integration" ]]; then
  export INTEGRATION_TESTS=true
  if [[ -z "${OPEN_AI_KEY:-}" ]]; then
    echo "ERROR: integration mode requires OPEN_AI_KEY in .env.test" >&2
    exit 1
  fi
else
  unset INTEGRATION_TESTS 2>/dev/null || true
  export ENV_TEST_PROPERTY="${ENV_TEST_PROPERTY:-1test1}"
  export JWT_SECRET_KEY="${JWT_SECRET_KEY:-test-jwt-secret-key-with-sufficient-length-for-hs256-algorithm}"
  export OPEN_AI_KEY="${OPEN_AI_KEY:-dummy-key}"
  export ELEVENLABS_API_KEY="${ELEVENLABS_API_KEY:-dummy-key}"
  export ELEVENLABS_VOICE_ID="${ELEVENLABS_VOICE_ID:-dummy-voice-id}"
fi

if [[ -t 1 ]] && [[ -z "${NO_COLOR:-}" ]]; then
  C_RESET=$'\033[0m'
  C_BOLD=$'\033[1m'
  C_DIM=$'\033[2m'
  C_CYAN=$'\033[36m'
  C_GREEN=$'\033[32m'
  C_RED=$'\033[31m'
  C_YELLOW=$'\033[33m'
  C_BLUE=$'\033[34m'
  USE_COLOR=1
else
  C_RESET=""
  C_BOLD=""
  C_DIM=""
  C_CYAN=""
  C_GREEN=""
  C_RED=""
  C_YELLOW=""
  C_BLUE=""
  USE_COLOR=0
fi

format_elapsed() {
  local total="$1"
  local minutes=$((total / 60))
  local seconds=$((total % 60))
  printf "%02d:%02d" "$minutes" "$seconds"
}

read_stats() {
  python3 "$PARSER" "$REPORTS_DIR" 2>/dev/null || echo '{"totals":{"tests":0,"passed":0,"failures":0,"errors":0,"skipped":0},"failed_cases":[]}'
}

print_progress() {
  local elapsed="$1"
  local stats_json="$2"
  local tests passed failures errors skipped
  read -r tests passed failures errors skipped < <(python3 -c "
import json, sys
t = json.loads(sys.argv[1])['totals']
print(t['tests'], t['passed'], t['failures'], t['errors'], t['skipped'])
" "$stats_json")

  local line
  if (( tests == 0 )); then
    line="${C_CYAN}${LABEL}${C_RESET}  ${C_DIM}$(format_elapsed "$elapsed")${C_RESET}  ${C_YELLOW}building...${C_RESET}"
  else
    line="${C_CYAN}${LABEL}${C_RESET}  ${C_DIM}$(format_elapsed "$elapsed")${C_RESET}  run: ${C_BOLD}${tests}${C_RESET}  passed: ${C_GREEN}${passed}${C_RESET}  failed: ${C_RED}${failures}${C_RESET}  errors: ${C_RED}${errors}${C_RESET}  skipped: ${C_YELLOW}${skipped}${C_RESET}"
  fi

  if [[ -t 1 ]]; then
    printf '\033[2K\r%s' "$line"
  elif (( elapsed - LAST_PROGRESS_AT >= 5 || elapsed == 0 )); then
    printf '%b\n' "$line"
    LAST_PROGRESS_AT=$elapsed
  fi
}

print_summary() {
  local elapsed="$1"
  local exit_code="$2"

  FINAL_STATS="$FINAL_STATS" ELAPSED="$elapsed" EXIT_CODE="$exit_code" LOG_FILE="$LOG_FILE" \
    C_RESET="$C_RESET" C_BOLD="$C_BOLD" C_DIM="$C_DIM" C_CYAN="$C_CYAN" C_GREEN="$C_GREEN" \
    C_RED="$C_RED" C_YELLOW="$C_YELLOW" C_BLUE="$C_BLUE" USE_COLOR="$USE_COLOR" \
    python3 <<'PY'
import json
import os

def c(name: str) -> str:
    if os.environ.get("USE_COLOR") != "1":
        return ""
    return os.environ.get(f"C_{name.upper()}", "")

data = json.loads(os.environ["FINAL_STATS"])
elapsed = os.environ["ELAPSED"]
exit_code = int(os.environ["EXIT_CODE"])
log_file = os.environ["LOG_FILE"]
totals = data["totals"]
failed_cases = data["failed_cases"]
broken = totals["failures"] + totals["errors"]
success = broken == 0 and exit_code == 0

print()
print(f"{c('DIM')}{'=' * 60}{c('RESET')}")
print(f"  {c('BOLD')}{totals['tests']}{c('RESET')} tests in {c('CYAN')}{elapsed}{c('RESET')}")
print(f"{c('DIM')}{'=' * 60}{c('RESET')}")
print(f"  Passed:  {c('GREEN')}{totals['passed']}{c('RESET')}")
print(f"  Failed:  {c('RED') if totals['failures'] else ''}{totals['failures']}{c('RESET')}")
print(f"  Errors:  {c('RED') if totals['errors'] else ''}{totals['errors']}{c('RESET')}")
print(f"  Skipped: {c('YELLOW') if totals['skipped'] else ''}{totals['skipped']}{c('RESET')}")
print()

if failed_cases:
    print(f"{c('RED')}{c('BOLD')}Failed tests ({len(failed_cases)}):{c('RESET')}")
    print(f"{c('DIM')}{'-' * 60}{c('RESET')}")
    for case in failed_cases:
        kind_color = c("RED") if case["kind"] == "FAIL" else c("YELLOW")
        print(f"  {kind_color}[{case['kind']}]{c('RESET')} {c('BOLD')}{case['class']}{c('RESET')}")
        print(f"         {case['name']}")
        if case["message"]:
            print(f"         {c('DIM')}{case['message']}{c('RESET')}")
        print()
elif broken == 0 and exit_code != 0:
    print(f"{c('RED')}Maven failed before tests finished.{c('RESET')}")
    print(f"{c('DIM')}See log: {log_file}{c('RESET')}")
    print()

if success:
    print(f"{c('GREEN')}{c('BOLD')}All tests passed.{c('RESET')}")
else:
    print(f"{c('RED')}{c('BOLD')}Result: FAILED{c('RESET')}  {c('DIM')}(details: target/surefire-reports/, log: {log_file}){c('RESET')}")
PY
}

mkdir -p "$ROOT/target"
: >"$LOG_FILE"
rm -rf "$REPORTS_DIR"
mkdir -p "$REPORTS_DIR"

printf '%b\n' "${C_CYAN}${LABEL}${C_RESET} — starting..."
printf '%b\n' "${C_DIM}Full Maven log: ${LOG_FILE}${C_RESET}"
echo ""

START_EPOCH=$(date +%s)
LAST_PROGRESS_AT=-5

TEST_CLASS="${TEST_CLASS:-com.ord.AllTestsSuite}"

mvn -Dtest="$TEST_CLASS" \
  -DINTEGRATION_TESTS="${INTEGRATION_TESTS:-false}" \
  -Dsurefire.parallel=none \
  -DforkCount=1 \
  -DreuseForks=false \
  -Dsurefire.printSummary=false \
  --batch-mode \
  -q \
  test >"$LOG_FILE" 2>&1 &
MVN_PID=$!

while kill -0 "$MVN_PID" 2>/dev/null; do
  NOW=$(date +%s)
  ELAPSED=$((NOW - START_EPOCH))
  STATS="$(read_stats)"
  print_progress "$ELAPSED" "$STATS"
  sleep 0.4
done

MVN_EXIT=0
wait "$MVN_PID" || MVN_EXIT=$?

END_EPOCH=$(date +%s)
ELAPSED=$((END_EPOCH - START_EPOCH))
FINAL_STATS="$(read_stats)"

printf '\033[2K\r'
print_summary "$(format_elapsed "$ELAPSED")" "$MVN_EXIT"

exit "$MVN_EXIT"
