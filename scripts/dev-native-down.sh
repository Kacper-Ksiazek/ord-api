#!/usr/bin/env bash
set -euo pipefail

# shellcheck source=scripts/dev-native-common.sh
source "$(dirname "${BASH_SOURCE[0]}")/dev-native-common.sh"

api_dev_down
printf '✅ native api stopped\n'
