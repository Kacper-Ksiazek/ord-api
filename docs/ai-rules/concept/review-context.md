# Review context

idea_gate: required

Automated idea review and code review read this file first. The full rules stay in `docs/ai-rules/`. Cite a rule file when you flag a violation. Do not invent a standard that is not written here or in those rules. Do not clone other repositories.

## Product

ORD API is the backend for an AI language-learning app: words, banks, games, and conversations. Each user's data is scoped to that user. Auth is an email one-time code and an opaque session cookie. The OpenAPI spec is the contract published to the frontend as `@kacper-ksiazek/ord-api-types`. See `concept/project-brief.md`.

The priority is consistency with the existing vertical slices and a controller integration test for a new endpoint.

## Stack

Kotlin, Spring Boot WebFlux, R2DBC, PostgreSQL, Flyway, Spring Security, SpringDoc OpenAPI, Kotest, Testcontainers.

## Code review invariants

- A feature flows `Controller → Facade → Service → Repository → Entity / Mapper / DTO`. A layer calls only the interface directly beneath it. See `architecture/layered-slice-chain.md` and `architecture/controllers-only-delegate.md`.
- User-owned entities implement `IdentifiableUserResource` and go through `UserResourceService`. See `architecture/entities-implement-identifiable-user-resource.md`.
- Production code does not block the reactive chain (`.block()`, `.blockFirst()`, `.blockLast()`, `.toFuture().get()`). See `reactive/never-block-the-reactive-chain.md`.
- A new HTTP endpoint ships with a controller integration test. See `testing/every-new-endpoint-gets-a-controller-integration-test.md`.
- OpenAPI annotations match the DTO. A breaking response change is a contract change for the frontend types package.

## Idea review

A change should extend an existing feature shape (conversation, word, game, bank) instead of introducing a new architectural pattern. Block a second pipeline for a job the current slice already owns, a blocking escape hatch, or an endpoint that is not user-scoped.

## Out of scope for code review

Whether the change should exist. That is the idea gate.
