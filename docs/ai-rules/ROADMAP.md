# AI Rules — Roadmap & Knowledge Snapshot

Living document. It records the detected stack, the rule categories, the repo areas each
category covers, and progress. Subagents check off items as they finish writing rules.

## Detected stack

- **Language:** Kotlin 2.1.21 (JVM target 17, Java 24 toolchain)
- **Framework:** Spring Boot 3.2.3, **WebFlux (fully reactive)**
- **Persistence:** Spring Data **R2DBC** + PostgreSQL; Flyway migrations; H2 (CI fallback)
- **Auth:** OTP (email) → JWT (jjwt) via cookie; reactive Spring Security
- **AI:** OpenAI integration — prompts (file-based templates), structured outputs, SSE
  streaming, per-operation GPT token-usage logging
- **API docs:** SpringDoc OpenAPI + Swagger UI; `openapi.json` exported; TS `types-package`
- **Testing:** JUnit 5 (Suite), Kotest assertions, Testcontainers (Postgres), WebTestClient
- **Build/Run:** Maven (`mvnw`), Docker / docker-compose, Heroku deploy via GitHub Actions
- **App domain:** AI-powered language/vocabulary learning (words, banks, games, conversations)

## Architecture summary

Strict vertical slicing under `com.ord`: `config/`, `core/`, `features/`, `exceptions/`,
`shared/`. Every feature follows
`Controller → Facade(interface+impl) → Service(interface+impl) → Repository(+CustomMethods/impl)
→ Entity / Mapper / DTO`. All resources are user-scoped via `IdentifiableUserResource` +
`UserResourceRepository` + `UserResourceService`.

## Categories

| Category | Covers (repo areas) | Status |
|----------|---------------------|--------|
| `general` | Top-level layout, package/file naming, `core` vs `features` vs `shared`, Kotlin file conventions | [x] (6) |
| `architecture` | The Controller→Facade→Service→Repository→Entity/Mapper/DTO layering; feature directory layout; base abstractions (`UserResourceService`, `UserResourceRepository`, `IdentifiableUserResource`, mappers) | [x] (9) |
| `kotlin` | Data classes, null handling (`?:`, `error(...)`), default args, `companion object` constants, extension functions, enums | [x] (8) |
| `reactive` | WebFlux + Reactor `Mono`/`Flux` usage, composition (`flatMap`/`map`/`zip`/`collectList`), no blocking, SSE streaming | [x] (8) |
| `persistence` | R2DBC repositories, `*CustomMethods` + `R2dbcEntityTemplate` raw SQL, enum/`Json` binding, `OffsetDateTime`→`Instant`, `@Table` entities, Flyway migration naming/conventions | [x] (11) |
| `api-design` | Controllers, REST path/verb/status conventions, request/response DTOs, Jakarta validation + custom validators, OpenAPI/Swagger annotations, OpenAPI contract & TS types-package | [x] (9) |
| `security-auth` | OTP→JWT flow, `@AuthenticatedUser` resolver, reactive Spring Security config, user-scoping of all data, anonymous vs authorized paths | [x] (9) |
| `ai-integration` | `OpenAIAPIClientService` request/streaming APIs, GPT token-usage logging per operation key, response parsing/validation callbacks, `BadGatewayException` handling for AI failures | [x] (8) |
| `prompts` | Prompt management: file-based `.md` templates under `resources/prompts/` (+ `guidelines.md`), `AvailablePrompts` enum registry, `Prompt`/`PromptCache` loading & `{{param}}` substitution, structured-output schemas (`StructuredOutputTemplate` + `structured_outputs/features/**`), wiring a prompt to its schema and operation key | [x] (9) |
| `error-handling` | `exceptions/REST/*` hierarchy, `RESTExceptionHandler` `@ControllerAdvice`, error response DTO, switchIfEmpty→NotFound pattern | [x] (7) |
| `testing` | Controller integration tests, `ControllerTestBase`, `*APIClient` wrappers, seeders/factories, Kotest matchers, Testcontainers, `AllTestsSuite` registration, AI smoke stubs | [x] (12) |
| `git-workflow` | Commit policy (never self-commit; ask + push), migration immutability, OpenAPI re-export discipline | [x] (7) |

## Progress

- [x] Repo analysis
- [x] Concept brief (`concept/project-brief.md`)
- [x] Roadmap
- [x] `general` rules
- [x] `architecture` rules
- [x] `kotlin` rules
- [x] `reactive` rules
- [x] `persistence` rules
- [x] `api-design` rules
- [x] `security-auth` rules
- [x] `ai-integration` rules
- [x] `prompts` rules
- [x] `error-handling` rules
- [x] `testing` rules
- [x] `git-workflow` rules
- [x] Pointer files (`.cursor/rules/ai-rules.mdc`, `CLAUDE.md`)
