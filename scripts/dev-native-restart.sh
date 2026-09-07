#!/usr/bin/env bash
set -euo pipefail

# shellcheck source=scripts/dev-native-common.sh
source "$(dirname "${BASH_SOURCE[0]}")/dev-native-common.sh"

was_running=false
if http_up "http://localhost:${API_PORT}${API_HEALTH_PATH}"; then
	was_running=true
fi

ensure_db_up
stop_docker_app_if_running
load_api_env
require_java

# Compile first — if it fails, keep the currently running API alive.
if ! api_dev_compile; then
	if [[ "$was_running" == true ]]; then
		printf '❌ compile failed — previous API left running on port %s\n' "$API_PORT" >&2
	else
		printf '❌ compile failed — API not started\n' >&2
	fi
	exit 1
fi

api_dev_down
api_dev_start

if api_dev_wait_healthy; then
	printf '✅ api refreshed at port %s (native, dev mode — tests skipped)\n' "$API_PORT"
	exit 0
fi

exit 1
