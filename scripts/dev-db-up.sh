#!/usr/bin/env bash
set -euo pipefail

# shellcheck source=scripts/dev-native-common.sh
source "$(dirname "${BASH_SOURCE[0]}")/dev-native-common.sh"

ensure_db_up
printf '✅ postgres started (docker)\n'
