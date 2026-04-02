# AI Codebase Overview

A reference document for AI agents working on this codebase.

---

## Stack

- **Language:** Kotlin 2.1.21
- **Framework:** Spring Boot 3.2.3 (WebFlux — reactive)
- **Data Access:** Spring Data R2DBC (reactive, PostgreSQL)
- **Database:** PostgreSQL with Flyway migrations
- **Testing:** JUnit 5, Kotest assertions, TestContainers
- **Build Tool:** Maven
- **Auth:** JWT (jjwt library)
- **API Docs:** SpringDoc OpenAPI + Swagger UI

---

## Project Structure

```
src/main/kotlin/com/ord/
├── config/                        # Spring configuration
├── core/                          # Core concerns (auth, user, language proficiency, tokens)
├── features/                      # Feature modules
│   ├── conversation/              # Conversation practice with AI
│   ├── game/                      # Game features
│   ├── word/                      # Word management
│   ├── bank/                      # Word banks
│   ├── bank_group/                # Bank groups
│   ├── quickly_added_words/       # Quick-add word feature
│   ├── user_activity_log/         # Activity tracking with points
│   └── ai_explainer/              # AI explanations
├── exceptions/                    # Custom exceptions (REST/NotFoundException, etc.)
└── shared/                        # Shared base models, repositories, mappers, services
```

---

## Architectural Layering

Every feature follows this strict layering:

```
Controller  →  Facade  →  Service  →  Repository  →  Entity
                                                        ↕
                                                      Mapper → DTO
```

1. **Controller** — request validation via `@Valid`, delegates to facade, returns `Mono<ResponseEntity<T>>`
2. **Facade** (interface + `impl/` subpackage) — orchestration, composition, error handling
3. **Service** (interface + `impl/` subpackage) — business logic, delegates to repository
4. **Repository** — Spring Data R2DBC; complex queries go in a `*CustomMethods` interface with an `impl/` class using `R2dbcEntityTemplate.databaseClient`
5. **Entity** — `@Table`-annotated data classes
6. **Mapper** — unidirectional `Entity → DTO` transformations (implements `UnidirectionalEntityMapper<E, D>`)

### Typical Feature Directory Layout

```
features/<feature>/
├── api/
│   ├── <Feature>Controller.kt
│   ├── facades/
│   │   ├── <Feature>Facade.kt           # interface
│   │   └── impl/
│   │       └── <Feature>FacadeImpl.kt    # @Service
│   └── requests/                         # request body DTOs
├── models/
│   ├── <entity>/
│   │   ├── <Entity>Entity.kt
│   │   ├── <Entity>DTO.kt
│   │   ├── <Entity>Mapper.kt
│   │   └── enums/
│   └── <sub_entity>/                     # child entities follow same pattern
├── repositories/
│   ├── <Entity>Repository.kt            # extends UserResourceRepository + CustomMethods
│   ├── <Entity>RepositoryCustomMethods.kt  # (sometimes inline in Repository.kt)
│   └── impl/
│       └── <Entity>RepositoryCustomMethodsImpl.kt  # @Repository, uses R2dbcEntityTemplate
├── services/
│   ├── <Feature>Service.kt              # interface
│   └── impl/
│       └── <Feature>ServiceImpl.kt      # @Service
└── validators/
    ├── annotations/                      # custom validation annotations
    └── <Validator>.kt                    # ConstraintValidator implementations
```

---

## Key Patterns

### Authentication

- Controller methods receive `@AuthenticatedUser user: UserDTO` (parameter annotated `@Parameter(hidden = true)`)
- All data is user-scoped — queries filter by `userId`
- Base interface `IdentifiableUserResource` provides `userId` field
- Base repository `UserResourceRepository` provides user-scoped CRUD

### Reactive Stack

- All endpoints return `Mono<ResponseEntity<T>>` or `Flux<T>` (streaming)
- Streaming endpoints (SSE) use `produces = [MediaType.TEXT_EVENT_STREAM_VALUE]`
- Composition via `Mono.zip()`, `flatMap`, `map`, `collectList()`

### R2DBC Custom Queries

Custom repository implementations use `R2dbcEntityTemplate`:
```kotlin
template.databaseClient
    .sql(query)
    .bind("paramName", value)
    .map { row -> /* map row to entity/DTO */ }
    .all()  // Flux
    .one()  // Mono
```

- Enum values are bound as `.name` strings (e.g., `.bind("type", type.name)`)
- Timestamps come back as `OffsetDateTime` from PostgreSQL and are converted to `Instant` via `.toInstant()`
- JSONB columns use `io.r2dbc.postgresql.codec.Json` type

### Sealed Interface DTOs

Polymorphic JSON serialization using Jackson `@JsonTypeInfo` + `@JsonSubTypes`:
```kotlin
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "sender")
@JsonSubTypes(
    JsonSubTypes.Type(value = ConversationUserMessageDTO::class, name = "USER"),
    JsonSubTypes.Type(value = ConversationAIMessageDTO::class, name = "AI"),
)
sealed interface ConversationMessageDTO { ... }
```

### Utility Objects

Pure stateless calculation objects (no Spring dependencies) live alongside models:
- `RecencyBucketCalculator` — time bucket computation, uses UTC, Monday as week start

---

## Database

### Migration Naming

Flyway migrations in `src/main/resources/db/migration/` follow `V<number>__<description>.sql`.

Latest migrations at time of writing: `V21__seed_root_user_conversation.sql`

### Core Tables (Conversation Feature)

```
conversations
├── id (UUID PK)
├── topic (TEXT)
├── additional_context (TEXT)
├── language (language_name ENUM)
├── proficiency_level (language_proficiency ENUM)
├── type (conversation_type ENUM)
├── ai_tone (conversation_tone ENUM)
├── ai_interlocutor_name (VARCHAR)
├── ai_interlocutor_avatar_id (VARCHAR 64)
├── user_id (UUID FK → users)
├── created_at (TIMESTAMP)
└── updated_at (TIMESTAMP)

conversation_messages
├── id (UUID PK)
├── message_order (INT)
├── content (TEXT)
├── sender (conversation_message_sender ENUM: USER | AI)
├── conversation_id (UUID FK → conversations)
└── created_at (TIMESTAMP)

conversation_user_message_analysis
├── id (UUID PK)
├── tutor_comment, corrected_message (TEXT)
├── grammar, vocabulary, naturalness, coherence_with_context (INT 0-10)
├── mistakes, strengths, suggestions (JSONB)
├── message_id (UUID FK → conversation_messages)
└── created_at (TIMESTAMP)

conversation_ai_message_learning_tips
├── id (UUID PK)
├── grammar_tips, vocabulary_tips, phrase_tips (JSONB)
├── message_id (UUID FK → conversation_messages)
└── created_at (TIMESTAMP)
```

---

## API Conventions

- **Base paths:** `/api/v1/<resource>/`
- **Trailing slash:** GET list endpoints use trailing slash (e.g., `GET /api/v1/conversations/`)
- **Response codes:** POST create → `201 CREATED`, GET → `200 OK`, DELETE → `204 NO_CONTENT`
- **Swagger tags:** numbered and descriptive (e.g., `"5. Conversations: Management"`)
- **Security:** `@SecurityRequirement(name = "bearer-jwt")` on controller class

---

## Testing

### Structure

- Controller integration tests in `src/test/kotlin/com/ord/controllers/`
- Unit tests alongside source in `src/test/kotlin/com/ord/features/`
- Test utilities in `src/test/kotlin/com/ord/testing_utils/`

### Patterns

- `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)` + `@AutoConfigureWebTestClient(timeout = "180000")`
- **API client wrappers** (e.g., `ConversationAPIClient`) encapsulate `WebTestClient` calls, return `APIClientResponse<T>`
- **Seeders** for test data generation
- **Request factories** for building request DTOs
- **Assertions:** Kotest matchers (`shouldBe`, `shouldHaveSize`, `shouldBeEmpty`, etc.)
- `MockedAuthenticatedUser` for auth context in tests

### API Client Pattern

```kotlin
class ConversationAPIClient(webClient: WebTestClient) : APITestClient(webClient) {
    val baseUrl = "/api/v1/conversations"

    fun getConversations(user: MockedAuthenticatedUser? = null, ...): APIClientResponse<List<ConversationSummaryDTO>?> {
        return get(url = "$baseUrl/", user = user, responseBodyType = ...)
    }
}
```

---

## Conversation Feature — Enums

| Enum | Values |
|------|--------|
| `ConversationType` | SMALL_TALK, SCENARIO_ROLEPLAY, EXAM_PRACTICE, TOPIC_EXPLORATION, OXFORD_DEBATE |
| `ConversationTone` | FRIENDLY, FORMAL, HUMOROUS, NEUTRAL, ENCOURAGING, CHALLENGING |
| `ConversationMessageSender` | USER, AI |
| `RecencyBucket` | TODAY, YESTERDAY, THIS_WEEK, THIS_MONTH, LATER |

---

## User Activity Log (Separate Feature)

`src/main/kotlin/com/ord/features/user_activity_log/` — tracks user actions with point values. `UserActivityType` enum covers game completions, word activities, and frequency tracking. No conversation-specific activity types exist yet.
