#!/usr/bin/env bash
# Verify committed openapi.json matches the live SpringDoc spec.
#
# Usage:
#   ./scripts/check-openapi.sh

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if ! docker info >/dev/null 2>&1; then
  echo "ERROR: Docker is not running. This check needs Testcontainers (PostgreSQL)." >&2
  exit 1
fi

export ENV_TEST_PROPERTY="${ENV_TEST_PROPERTY:-1test1}"
export OPEN_AI_KEY="${OPEN_AI_KEY:-dummy-key}"
export ELEVENLABS_API_KEY="${ELEVENLABS_API_KEY:-dummy-key}"
export ELEVENLABS_VOICE_ID="${ELEVENLABS_VOICE_ID:-dummy-voice-id}"

mvn -Dtest=com.ord.openapi.TestOpenApiSpec \
  -DINTEGRATION_TESTS=false \
  -Dsurefire.parallel=none \
  -DforkCount=1 \
  -DreuseForks=false \
  --batch-mode \
  -q \
  test
