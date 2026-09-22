#!/usr/bin/env bash
# Render the OTP email template with sample placeholders and open it in a browser.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TEMPLATE="$ROOT/src/main/resources/templates/otp-email.html"
PREVIEW_DIR="${TMPDIR:-/tmp}"
PREVIEW_FILE="$PREVIEW_DIR/ord-otp-email-preview.html"

OTP_CODE="${OTP_CODE:-123456}"
YEAR="$(date +%Y)"

if [[ ! -f "$TEMPLATE" ]]; then
  echo "Template not found: $TEMPLATE" >&2
  exit 1
fi

if [[ ! "$OTP_CODE" =~ ^[0-9]{6}$ ]]; then
  echo "OTP_CODE must be exactly 6 digits (got: $OTP_CODE)" >&2
  exit 1
fi

build_otp_cells() {
  local code="$1"
  local i ch content
  local cell_style="width:48px;height:56px;border:1px solid #e7e4dc;border-radius:10px;background-color:#ffffff;font-size:24px;font-weight:500;color:#1c1b18;text-align:center;vertical-align:middle;"
  local cells=""

  for ((i = 0; i < 6; i++)); do
    ch="${code:i:1}"
    content="$ch"
    cells+="<td align=\"center\" style=\"${cell_style}\">${content}</td>"
  done

  printf '%s' "$cells"
}

OTP_CELLS="$(build_otp_cells "$OTP_CODE")"

sed \
  -e "s/{{OTP_CODE}}/${OTP_CODE}/g" \
  -e "s/{{YEAR}}/${YEAR}/g" \
  -e "s|{{OTP_CELLS}}|${OTP_CELLS}|g" \
  "$TEMPLATE" > "$PREVIEW_FILE"

echo "Preview: $PREVIEW_FILE"

if command -v open >/dev/null 2>&1; then
  open "$PREVIEW_FILE"
elif command -v xdg-open >/dev/null 2>&1; then
  xdg-open "$PREVIEW_FILE" >/dev/null 2>&1 || true
else
  echo "Install 'open' or 'xdg-open' to launch a browser automatically." >&2
fi
