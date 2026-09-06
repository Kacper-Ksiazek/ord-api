#!/usr/bin/env bash
# Shared helpers for native (non-Docker) API dev — DB still runs in Docker.

ORD_API_DIR="${ORD_API_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)}"
COMPOSE_DEV="$ORD_API_DIR/docker-compose.yaml"
API_PORT="${API_PORT:-8080}"
API_HEALTH_PATH="${API_HEALTH_PATH:-/api/v1/health-check}"
RUNTIME_DIR="$ORD_API_DIR/.runtime"
API_DEV_PID_FILE="$RUNTIME_DIR/api-dev.pid"
API_DEV_LOG_FILE="$RUNTIME_DIR/api-dev.log"

# Dev mode: main sources only — never compile or run tests (broken tests are OK).
MVN_DEV_ARGS=(
	-Dmaven.test.skip=true
	-DskipITs
)

http_up() {
	local url="$1"
	local timeout="${2:-2}"

	curl -sfL --max-time "$timeout" "$url" >/dev/null 2>&1
}

ensure_runtime_dir() {
	mkdir -p "$RUNTIME_DIR"
}

require_docker() {
	if ! docker info >/dev/null 2>&1; then
		printf '❌ docker is not available\n' >&2
		exit 1
	fi
}

require_java() {
	if ! command -v java >/dev/null 2>&1; then
		printf '❌ java not found — JDK 24 required\n' >&2
		exit 1
	fi
}

resolve_mvn_cmd() {
	if [[ -x "$ORD_API_DIR/mvnw" ]] && [[ -f "$ORD_API_DIR/.mvn/wrapper/maven-wrapper.properties" ]]; then
		printf '%s' "$ORD_API_DIR/mvnw"
	elif command -v mvn >/dev/null 2>&1; then
		printf '%s' mvn
	else
		printf '❌ neither working mvnw nor mvn found (install Maven or run: mvn wrapper:wrapper)\n' >&2
		exit 1
	fi
}

load_compose_env() {
	local env_file="$ORD_API_DIR/.env"

	if [[ -f "$env_file" ]]; then
		# shellcheck disable=SC1090
		set -a
		source "$env_file"
		set +a
	fi

	# Silence docker compose warnings when only the db service is started.
	export SMTP_HOST="${SMTP_HOST:-}"
	export SMTP_PORT="${SMTP_PORT:-}"
	export SMTP_USERNAME="${SMTP_USERNAME:-}"
	export SMTP_PASSWORD="${SMTP_PASSWORD:-}"
}

load_api_env() {
	local env_file="$ORD_API_DIR/.env"

	if [[ ! -f "$env_file" ]]; then
		printf '❌ ord-api .env not found at %s\n' "$env_file" >&2
		printf '   Copy from README and fill in secrets.\n' >&2
		exit 1
	fi

	load_compose_env

	export SPRING_PROFILES_ACTIVE="${SPRING_PROFILE:-local}"
	export DATABASE_URL="postgres://${DOCKER_DB_USER}:${DOCKER_DB_PASSWORD}@localhost:5432/${DOCKER_DB_NAME}"
	export JWT_SECRET_KEY="${JWT_SECRET_KEY:-dev-secret-key-change-in-production}"
	export CORS_ALLOWED_ORIGINS="${CORS_ALLOWED_ORIGINS:-http://localhost:5173}"
	export ENV_TEST_PROPERTY="${ENV_TEST_PROPERTY:-1test1}"
	export PORT="${PORT:-$API_PORT}"
}

compose_has_running_services() {
	local compose_file="$1"

	[[ -f "$compose_file" ]] || return 1
	docker compose -f "$compose_file" ps --status running -q 2>/dev/null | grep -q .
}

stop_docker_app_if_running() {
	if [[ ! -f "$COMPOSE_DEV" ]]; then
		return 0
	fi

	if docker compose -f "$COMPOSE_DEV" ps --status running app -q 2>/dev/null | grep -q .; then
		printf '⚠️  stopping docker app container (native api uses port %s)\n' "$API_PORT"
		docker compose -f "$COMPOSE_DEV" stop app
	fi
}

stop_process_tree() {
	local pid="$1"
	local child

	[[ -n "$pid" ]] || return 0
	kill "$pid" 2>/dev/null || true
	while read -r child; do
		[[ -n "$child" ]] && stop_process_tree "$child"
	done < <(pgrep -P "$pid" 2>/dev/null || true)
}

api_dev_down() {
	if [[ ! -f "$API_DEV_PID_FILE" ]]; then
		return 0
	fi

	local pid
	pid="$(<"$API_DEV_PID_FILE")"
	stop_process_tree "$pid"
	rm -f "$API_DEV_PID_FILE"
}

ensure_db_up() {
	require_docker

	if [[ ! -f "$COMPOSE_DEV" ]]; then
		printf '❌ %s not found\n' "$COMPOSE_DEV" >&2
		exit 1
	fi

	load_compose_env
	docker compose -f "$COMPOSE_DEV" up -d db
}

api_dev_start() {
	local mvn
	mvn="$(resolve_mvn_cmd)"
	ensure_runtime_dir

	: >"$API_DEV_LOG_FILE"

	(
		cd "$ORD_API_DIR"
		nohup "$mvn" -q spring-boot:run "${MVN_DEV_ARGS[@]}" >>"$API_DEV_LOG_FILE" 2>&1 &
		printf '%s' "$!" >"$API_DEV_PID_FILE"
	)
}

api_dev_wait_healthy() {
	local health_url="http://localhost:${API_PORT}${API_HEALTH_PATH}"
	local attempt=0

	while [[ "$attempt" -lt 90 ]]; do
		if http_up "$health_url"; then
			return 0
		fi
		attempt=$((attempt + 1))
		sleep 1
	done

	return 1
}
