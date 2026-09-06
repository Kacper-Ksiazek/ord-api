#!/usr/bin/env bash
set -euo pipefail

# shellcheck source=scripts/dev-native-common.sh
source "$(dirname "${BASH_SOURCE[0]}")/dev-native-common.sh"

api_dev_down
ensure_db_up
stop_docker_app_if_running
load_api_env
require_java

api_dev_start

if api_dev_wait_healthy; then
	printf '✅ api refreshed at port %s (native, dev mode — tests skipped)\n' "$API_PORT"
	exit 0
fi

printf '❌ api failed to restart — see %s\n' "$API_DEV_LOG_FILE" >&2
exit 1
