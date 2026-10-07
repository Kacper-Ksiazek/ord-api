# Upstream Impact Report — Format Specification (v1)

Specification for the Cursor PR automation that summarizes API changes relevant to upstream
consumers — primarily the SvelteKit frontend and the `@kacper-ksiazek/ord-api-types` package.

## Automation behavior (operational rules)

| Rule | Value |
|------|-------|
| **Report language** | English |
| **Report depth** | Always full report (all sections below), even when the only contract change is `openapi.json` |
| **Draft PRs** | Ignore — do not run on draft pull requests |
| **Comment threading** | Single thread per PR — find the existing automation comment and **edit/replace** it; never post a second top-level comment on subsequent pushes |

### Single-thread marker

Every automation comment must start with this exact HTML comment (invisible in rendered Markdown):

```html
<!-- ord-upstream-impact-report -->
```

On re-runs (PR opened, synchronize, reopened), search PR comments for that marker. If found,
update that comment in place. If not found, create one new comment.

---

## Report template

Copy this structure verbatim. Use `—` (em dash) for empty table rows. Do not omit sections.

```markdown
<!-- ord-upstream-impact-report -->
## 📡 Upstream impact report

**PR:** #{number} — {title}
**Branch:** `{head}` → `{base}`
**Verdict:** `BREAKING` | `ADDITIVE` | `BEHAVIORAL` | `NO_UPSTREAM_IMPACT`
**Types package:** `openapi.json` changed — publish on merge to `main` expected | not affected

---

### TL;DR
{1–2 sentences: what the frontend must know. If nothing applies, state explicitly: "No API contract changes."}

---

### API surface

#### New endpoints
| Method | Path | Auth | Response | Notes |
|--------|------|------|----------|-------|
| | | | | |

#### Changed endpoints
| Method | Path | What changed | Breaking? |
|--------|------|--------------|-----------|
| | | | |

#### Removed / deprecated endpoints
| Method | Path | Migration hint |
|--------|------|----------------|
| | | |

---

### Schemas & enums

#### Request / response DTOs
| Schema | Change | Fields | Breaking? |
|--------|--------|--------|-----------|
| | | | |

#### Exported enums (`@ExportToOpenAPI`)
| Enum | Change | Values added/removed |
|------|--------|----------------------|
| | | |

---

### Auth & security
- {new paths in `ANONYMOUS_PATHS` / `AUTHORIZED_PATHS`, auth requirement changes, new public endpoints}
- Cookie: `AUTH-TOKEN` (HttpOnly) — **unchanged** | {describe change}

---

### Errors & edge cases
| Status | When | Frontend action |
|--------|------|-----------------|
| | | |

---

### Streaming & non-JSON responses
| Endpoint | Media type | Client notes |
|----------|------------|--------------|
| | | |

---

### Frontend checklist
- [ ] Update `@kacper-ksiazek/ord-api-types` after merge to `main` (if `openapi.json` changed)
- [ ] {concrete action, e.g. "add TTS client with stream/blob response handling"}

---

### Out of scope (intentionally omitted)
{short list: internal refactors, tests, DB migrations without API semantics change, docs/ai-rules, etc.}
```

---

## Classification rules (for the automation agent)

### Include in the report

| Category | Signals in the diff |
|----------|----------------------|
| **HTTP contract** | `*Controller.kt`, `*Request.kt`, `*DTO.kt`, `openapi.json` |
| **TypeScript enums** | `@ExportToOpenAPI`, new/changed enum schemas |
| **Auth** | `SecurityConfiguration.kt`, `@SecurityRequirement`, `ANONYMOUS_PATHS` / `AUTHORIZED_PATHS` |
| **Client-visible errors** | new HTTP status codes, new exception types in `@ControllerAdvice` |
| **Streaming** | `TEXT_EVENT_STREAM`, `audio/*`, custom `produces` |
| **Types publishing** | `openapi.json` change → merge to `main` triggers `@kacper-ksiazek/ord-api-types` on GitHub Packages |

### Exclude (list under "Out of scope")

- Services, repositories, facades with no public API change
- Flyway migrations unless they alter response semantics
- Tests, CI, `docs/ai-rules/*`
- Internal config (API keys, connection pools) with no request/response impact

### Verdict definitions

| Verdict | When |
|---------|------|
| `BREAKING` | removed endpoint, new required field, field type change, removed enum value, auth change (public ↔ authenticated) |
| `ADDITIVE` | new endpoints, optional fields, new enum values — existing client keeps working |
| `BEHAVIORAL` | same contract, different semantics (sorting, defaults, limits, pagination) |
| `NO_UPSTREAM_IMPACT` | no contract or auth changes |

When verdict is `NO_UPSTREAM_IMPACT`, still render the **full** template — populate tables with `—`
and explain in TL;DR and Out of scope.

---

## Project context (quick reference)

- **Frontend:** SvelteKit + Axios (`withCredentials: true`) + TanStack Query
- **Auth:** JWT in HttpOnly cookie `AUTH-TOKEN` (not `Authorization: Bearer`)
- **Types:** generated from `openapi.json` → `@kacper-ksiazek/ord-api-types` (GitHub Packages)
- **SSE endpoints:** conversations, AI explainer, word AI — `text/event-stream`
- **Non-JSON:** e.g. TTS `POST /api/v1/tts/speak` → `audio/mpeg` stream

---

## Filled example — `feat/tts-elevenlabs`

```markdown
<!-- ord-upstream-impact-report -->
## 📡 Upstream impact report

**PR:** #42 — feat: add TTS speak endpoint via ElevenLabs streaming
**Branch:** `feat/tts-elevenlabs` → `main`
**Verdict:** `ADDITIVE`
**Types package:** `openapi.json` changed — publish on merge to `main` expected

---

### TL;DR
New TTS endpoint synthesizes speech from assistant reply text. The frontend must handle an
`audio/mpeg` response stream (not JSON). Update the types package after merge.

---

### API surface

#### New endpoints
| Method | Path | Auth | Response | Notes |
|--------|------|------|----------|-------|
| `POST` | `/api/v1/tts/speak` | cookie `AUTH-TOKEN` | `audio/mpeg` (stream) | body: `SpeakRequest` |

#### Changed endpoints
| Method | Path | What changed | Breaking? |
|--------|------|--------------|-----------|
| — | — | — | — |

#### Removed / deprecated endpoints
| Method | Path | Migration hint |
|--------|------|----------------|
| — | — | — |

---

### Schemas & enums

#### Request / response DTOs
| Schema | Change | Fields | Breaking? |
|--------|--------|--------|-----------|
| `SpeakRequest` | **added** | `text: string` (required, 1–5000 chars) | no |

#### Exported enums (`@ExportToOpenAPI`)
| Enum | Change | Values added/removed |
|------|--------|----------------------|
| — | — | — |

---

### Auth & security
- New path `/api/v1/tts/**` added to `AUTHORIZED_PATHS` — requires authentication.
- Cookie `AUTH-TOKEN` — **unchanged**.

---

### Errors & edge cases
| Status | When | Frontend action |
|--------|------|-----------------|
| `400` | blank or oversized `text` | validate in UI (max 5000) |
| `401` | missing session | redirect to login |
| `502` | ElevenLabs unavailable | show fallback / retry |

---

### Streaming & non-JSON responses
| Endpoint | Media type | Client notes |
|----------|------------|--------------|
| `POST /api/v1/tts/speak` | `audio/mpeg` | use `fetch` stream reader or blob URL for `<audio>`; `withCredentials: true` |

---

### Frontend checklist
- [ ] After merge: `pnpm update @kacper-ksiazek/ord-api-types`
- [ ] Add TTS client (e.g. mutation with blob/stream response handling)
- [ ] UI: "play" button on AI replies in conversations
- [ ] Handle `502` gracefully (hide TTS when unavailable)

---

### Out of scope (intentionally omitted)
- `ElevenLabsTTSClientService` implementation (internal client)
- Integration tests (`TestTtsController`)
- `@SecurityRequirement` alignment on existing controllers (OpenAPI docs only, no runtime change)
- DB migrations, `docs/ai-rules`
```
