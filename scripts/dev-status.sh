#!/usr/bin/env bash
# Print local dev stack status (docker, api, front, storybook).
#
# Usage: ./scripts/dev-status.sh
# Env:   ORD_API_DIR, ORD_FRONTEND_DIR, API_PORT, FRONT_PORT, STORYBOOK_PORT

set -euo pipefail

ORD_API_DIR="${ORD_API_DIR:-$HOME/workspace/ord-api}"
ORD_FRONTEND_DIR="${ORD_FRONTEND_DIR:-$HOME/workspace/ord-frontend}"
API_PORT="${API_PORT:-8080}"
FRONT_PORT="${FRONT_PORT:-5173}"
STORYBOOK_PORT="${STORYBOOK_PORT:-6006}"

COMPOSE_DEV="$ORD_API_DIR/docker-compose.yaml"
COMPOSE_E2E="$ORD_API_DIR/docker-compose.e2e.yml"

http_up() {
	curl -sf --max-time 2 "$1" >/dev/null 2>&1
}

compose_has_running_services() {
	local compose_file="$1"

	[[ -f "$compose_file" ]] || return 1
	docker compose -f "$compose_file" ps --status running -q 2>/dev/null | grep -q .
}

resolve_api_port() {
	local mapped_port=""

	if [[ -f "$COMPOSE_DEV" ]] && compose_has_running_services "$COMPOSE_DEV"; then
		mapped_port="$(docker compose -f "$COMPOSE_DEV" port app 8080 2>/dev/null | cut -d: -f2 || true)"
	fi

	if [[ -n "$mapped_port" ]]; then
		printf '%s' "$mapped_port"
	else
		printf '%s' "$API_PORT"
	fi
}

print_docker_status() {
	if docker info >/dev/null 2>&1; then
		printf '%-11s available\n' 'docker:'
	else
		printf '%-11s not available\n' 'docker:'
	fi
}

print_api_status() {
	local health_url port

	port="$(resolve_api_port)"
	health_url="http://localhost:${port}/api/v1/health-check"

	if ! http_up "$health_url"; then
		printf '%-11s down\n' 'api:'
		return
	fi

	if [[ -f "$COMPOSE_E2E" ]] && compose_has_running_services "$COMPOSE_E2E"; then
		printf '%-11s running (e2e mode)\n' 'api:'
		return
	fi

	printf '%-11s running at port %s\n' 'api:' "$port"
}

print_http_service_status() {
	local name="$1"
	local port="$2"

	if http_up "http://localhost:${port}/"; then
		printf '%-11s running at port %s\n' "${name}:" "$port"
	else
		printf '%-11s down\n' "${name}:"
	fi
}

print_docker_status
print_api_status
print_http_service_status 'front' "$FRONT_PORT"
print_http_service_status 'storybook' "$STORYBOOK_PORT"
