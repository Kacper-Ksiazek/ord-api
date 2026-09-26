# ORD API — Bruno

Manual API collection for local development and smoke testing.

## Setup

1. Open `bruno/ord-api` in [Bruno](https://www.usebruno.com/)
2. Select the **local** environment
3. Ensure the API is running with `OTP_WHITELISTED_EMAILS=dev@example.com`

## Auth flow

1. `auth/otp-request`
2. `auth/otp-verify` — sets `AUTH-TOKEN` cookie
3. `users/get-me` — check `isAccountInitialized`, `name`, languages, etc.

Clear Bruno cookies before step 1 if you need a fresh OTP session.

## Conversations

After login, use `workflows/conversation-session` as a runbook, or explore:

- `conversations/auxiliary/` — topic & interlocutor helpers
- `conversations/crud/` — list, create, get, delete
- `conversations/activity/` — 90-day overview
- `conversations/ongoing/` — live session (SSE + messages)

`conversations/crud/create` sets `conversationId` for downstream requests.

## Auth

JWT lives in the **AUTH-TOKEN** HttpOnly cookie (not `Authorization: Bearer`).

## Organization

Folder layout follows `docs/ai-rules/bruno/` — domain folders with facade/controller subfolders.
