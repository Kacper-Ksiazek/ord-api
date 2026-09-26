# CLAUDE.md

Guidance for AI agents working in the ORD API repository.

## AI Rules knowledge base

This repository's coding rules live in **`docs/ai-rules/`**, organized as **one directory per
category**, with **one rule per `.md` file**. Consult the relevant category before writing or
changing code — this project prioritizes **strict consistency with existing patterns**.

- **Project concept / business context:** `docs/ai-rules/concept/project-brief.md`
- **Agent behavior:** `docs/ai-rules/concept/agent-behavior.md`
- **Knowledge snapshot & category map:** `docs/ai-rules/ROADMAP.md`

### Categories (`docs/ai-rules/<category>/`)

| Category | Scope |
|----------|-------|
| `general` | Project layout, naming, `core` vs `features` vs `shared` |
| `architecture` | Controller→Facade→Service→Repository→Entity/Mapper/DTO + base abstractions |
| `kotlin` | Kotlin language conventions |
| `reactive` | WebFlux + Reactor (`Mono`/`Flux`), no blocking, SSE streaming |
| `persistence` | R2DBC repos, raw SQL custom methods, entities, Flyway migrations |
| `api-design` | REST conventions, DTOs, validation, OpenAPI/Swagger contract |
| `security-auth` | OTP→session-cookie auth, `@AuthenticatedUser`, user-scoping |
| `ai-integration` | OpenAI client usage, token-usage logging |
| `prompts` | Prompt template management & structured-output schemas |
| `error-handling` | REST exceptions & centralized `@ControllerAdvice` handling |
| `testing` | Controller integration tests, Kotest, Testcontainers, suites |
| `git-workflow` | Commit policy, migration immutability, OpenAPI re-export |
| `bruno` | Bruno collection organization (`bruno/ord-api/`) |

## Critical reminders

- **Commits need approval.** Stop, summarize, ask the user, then push after commit
  (see `concept/agent-behavior.md`, `git-workflow`, `.clinerules`).
- Stack is **fully reactive** (WebFlux + R2DBC) — never block the event loop in main code.
- New endpoints must ship with controller integration tests, registered into `AllTestsSuite`.
