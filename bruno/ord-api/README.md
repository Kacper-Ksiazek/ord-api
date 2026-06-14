# ORD API — Bruno Collection

Manual testing and debugging collection for all `/api/v1` endpoints.

## Requirements

- [Bruno](https://www.usebruno.com/) desktop app
- Running ORD API (`DATABASE_URL`, `JWT_SECRET_KEY`, `ENV_TEST_PROPERTY=1test1`)
- For local OTP: `OTP_WHITELISTED_EMAILS=dev@example.com` and code `123456`

## Quick start

1. Open this folder in Bruno (`bruno/ord-api`)
2. Select **local** environment
3. Run `00-setup/health-check`
4. Run `01-auth/otp-request` then `01-auth/otp-verify`
5. Run `02-users/get-me` (cookie `AUTH-TOKEN` is sent automatically)

## Authentication

JWT is stored in the **AUTH-TOKEN** HttpOnly cookie (not `Authorization: Bearer`).
After `otp-verify`, Bruno stores the cookie in its cookie jar for `localhost:8080`.

**Anonymous-only endpoints** (must NOT send AUTH-TOKEN):
- `POST /auth/otp-request`, `POST /auth/otp-verify`
- `GET /health-check`
- `POST /public/quickly-added-words/bulk-create`

Clear cookies or run `logout` before re-authenticating via OTP.

## SSE endpoints

These return `text/event-stream`. Enable response streaming in Bruno and set `Accept: text/event-stream`:

- `04-words/ai/suggest-vocabulary-sse`
- `08-conversations/suggest-topics-sse`
- `08-conversations/ongoing/ai-initialize-sse`
- `08-conversations/ongoing/ai-request-message-sse`
- `09-ai-explainer/explain-phrase-sse`

Requires `OPEN_AI_KEY` on the server. AI calls may take a long time.

Fallback: `curl -N -b cookies.txt -H "Accept: text/event-stream" ...`

## Workflows

See `99-workflows/` for step-by-step debug sequences.

## Variables (chained via post-response scripts)

| Variable | Set by |
|----------|--------|
| userId | otp-verify |
| wordId, bankId | create word, get-many-words |
| qawId | create QAW |
| conversationId | create conversation |
| gameId | game start endpoints |

## Sync policy

When API changes: update Kotlin/OpenAPI, run `make openapi`, then update matching `.bru` files.

## Swagger UI

`http://localhost:8080/swagger-ui.html` (Basic Auth: admin/admin)
