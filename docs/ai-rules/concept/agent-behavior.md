# Agent behavior

How AI assistants (Cursor, Claude Code, Cloud Agents) should work in **ord-api**.
Stack and layering rules live in the other `docs/ai-rules/` categories; this file covers
**communication, process, precedence, and tone**.

Human-readable summary: prefer short, practical guidance (“simply …”) over shouting `NEVER`.

## Rule precedence

When instructions disagree, use this order unless the user clearly overrides in chat:

1. **Current user message** (latest intent wins over older turns).
2. **`.clinerules`** and **`docs/ai-rules/git-workflow/`** — especially commit approval.
3. **`docs/ai-rules/`** — technical conventions for Kotlin, WebFlux, tests, OpenAPI, etc.
4. **Mandatory skills/tools for the work** — e.g. Aikido scan after editing **source code**;
   Svelte MCP when editing **`.svelte`** files; Jira skill before **writing** to Jira.
5. **Generic agent or platform defaults** (e.g. Cloud Agent “commit and open a PR”).

If (4) or (5) conflicts with (2), follow **(2)** and tell the user briefly what clashed.

## Simply read the context first

Understand the task and conversation history before reaching for tools or MCP servers.
Use skills when the task matches their trigger — not on every message.

## Simply use the environment

Run commands and inspect output yourself. If something fails, diagnose and retry rather
than guessing. Prefer project `Makefile` targets (`make test`, `make openapi`, etc.) when
they exist.

## Simply communicate like a teammate

Be concise and precise. Use full sentences. Link paths and URLs in full. When pointing at
repo code, use fenced citations with line numbers (`startLine:endLine:path`) so the user
can jump in the editor.

## Simply keep the diff small

Change only what the task needs. Match naming, layering, and patterns from a comparable
existing feature (e.g. `conversation`, `word`). Do not introduce new architectural styles
without an explicit request.

## Simply test where risk is real

- **New or changed HTTP endpoints:** add a controller integration test and register it in
  `AllTestsSuite` (see `docs/ai-rules/testing/`).
- **Small refactors or docs-only work:** do not add tests that only restate the obvious.

Production code stays non-blocking on the reactive chain; **tests** may use `.block()` in
`src/test` (see `docs/ai-rules/testing/block-reactive-chains-in-tests.md`).

## Simply protect secrets

Never commit API keys, passwords, or `.env` with real values. Use `.env.example` patterns
from `docs/ai-rules/git-workflow/never-commit-secrets-only-env-example.md`.

## Simply keep the API contract honest

When request/response DTOs change, re-export OpenAPI and remember the TypeScript
`types-package` consumer (`docs/ai-rules/git-workflow/re-export-openapi-after-changing-dtos.md`).

## Simply respect AI cost

Prefer `make test` (AI stubs) over live OpenAI unless the user asks for `make test-live`
or the task truly requires real model output. Token usage is logged per operation in this
app — avoid redundant calls.

## Git in this repo

Detailed steps:

- [`never-commit-without-explicit-approval.md`](../git-workflow/never-commit-without-explicit-approval.md)
- [`push-immediately-after-approved-commits.md`](../git-workflow/push-immediately-after-approved-commits.md)

**In short:** finish the work → summarize → ask before `git commit` → push right after
approval.

## Related concept

- [`project-brief.md`](project-brief.md) — product domain and why consistency matters here.
