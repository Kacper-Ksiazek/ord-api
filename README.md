# ORD API

**AI-powered language-learning backend — fully reactive Kotlin / Spring WebFlux.**

ORD is the backend for a vocabulary-learning app where users capture words, practice through AI-generated games and conversations, and get on-demand explanations — all scoped to their CEFR proficiency level. Built as a personal learning project, it follows production-grade patterns: strict layering, a first-class OpenAPI contract, and full controller integration test coverage.

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.21-7F52FF?logo=kotlin&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?logo=springboot&logoColor=white)
![WebFlux](https://img.shields.io/badge/WebFlux-reactive-6DB33F)
![R2DBC](https://img.shields.io/badge/R2DBC-non--blocking-6DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-migrations-CC0200?logo=flyway&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-integrated-412991?logo=openai&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-OTP_→_JWT-000000)
![OpenAPI](https://img.shields.io/badge/OpenAPI-contract-6BA539?logo=openapiinitiative&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-containerized-2496ED?logo=docker&logoColor=white)
![Testcontainers](https://img.shields.io/badge/Testcontainers-integration_tests-000000)

## Highlights

- **Fully non-blocking stack** — Spring WebFlux + R2DBC end-to-end; SSE streaming for AI responses
- **OTP → JWT auth** — email one-time codes, JWT delivered via HTTP-only cookie; reactive Spring Security
- **AI-native features** — OpenAI for generation, review, and structured outputs; ElevenLabs for TTS; per-operation GPT token-usage logging
- **Strict layered architecture** — `Controller → Facade → Service → Repository → Entity / Mapper / DTO` with vertical feature slicing
- **Contract-first API** — SpringDoc OpenAPI spec exported to `openapi.json`; TypeScript types auto-published on change
- **Integration-tested** — 15+ controller test suites via Kotest + Testcontainers (PostgreSQL)

## Features

| Domain | What it does |
|--------|-------------|
| **Conversations** | Scenario-based AI chat with customizable interlocutors, per-message analysis, and learning tips |
| **Games** | AI-generated practice: crossword, sentence writing, words typing — with AI review |
| **Quickly Added Words** | Fast vocabulary capture flow, including a public anonymous entry point |
| **AI Explainer** | On-demand phrase explanations |
| **TTS** | Text-to-speech via ElevenLabs |
| **Words & details** | Vocabulary CRUD with AI-generated definitions, examples, and manual enrichment |
| **Language proficiencies** | Per-user CEFR levels (A1–C2) gating generative features |
| **Banks & groups** | Organize words into collections and bank groups |

## Architecture

Every feature follows the same vertical slice under `com.ord`. All user data is scoped via `IdentifiableUserResource` + `UserResourceRepository` + `UserResourceService`.

```
src/main/kotlin/com/ord/
├── config/          # Security, OpenAPI, properties
├── core/            # Auth, users, words, AI client, TTS, proficiencies
├── features/        # Banks, games, conversations, QAW, explainer, …
├── shared/          # Base abstractions, prompts, validators, DTOs
└── exceptions/      # REST exception hierarchy + @ControllerAdvice
```

```mermaid
flowchart LR
    Client["Client"]
    Security["JWT Security Filter"]
    Controller["Controller"]
    Facade["Facade"]
    Service["Service"]
    Repo["R2DBC Repository"]
    DB[("PostgreSQL")]
    OpenAI["OpenAI API"]
    ElevenLabs["ElevenLabs TTS"]
    SMTP["SMTP / OTP"]

    Client --> Security --> Controller --> Facade --> Service
    Service --> Repo --> DB
    Service --> OpenAI
    Service --> ElevenLabs
    Service --> SMTP
```

**Prompt system** — file-based `.md` templates under `resources/prompts/`, registered via `AvailablePrompts`, with structured-output JSON schemas for reliable AI responses.

## Tech stack

| Layer | Choices |
|-------|---------|
| **Language / framework** | Kotlin 2.1.21, Spring Boot 3.2.3, WebFlux, Reactor (`Mono` / `Flux`) |
| **Persistence** | Spring Data R2DBC, PostgreSQL 16, Flyway migrations |
| **Auth** | OTP (email) → JWT (jjwt) via cookie, reactive Spring Security |
| **AI** | OpenAI Responses API, structured outputs, SSE streaming, ElevenLabs TTS |
| **API docs** | SpringDoc OpenAPI + Swagger UI |
| **Testing** | JUnit 5 Suite, Kotest, Testcontainers, WebTestClient |
| **Build / deploy** | Maven, Docker multi-stage build, GitHub Actions → Heroku |

## Getting started

### Prerequisites

- **JDK 24** (or compatible with the Maven toolchain in `pom.xml`)
- **Docker** & Docker Compose

### Run locally

1. Create a `.env` file in the project root (used by `docker-compose.yaml`):

```bash
DOCKER_DB_NAME=ord
DOCKER_DB_USER=ord
DOCKER_DB_PASSWORD=ord
JWT_SECRET_KEY=change-me
OPEN_AI_KEY=sk-...
ELEVENLABS_API_KEY=...
ELEVENLABS_VOICE_ID=...
EMAIL_FROM=noreply@example.com
SMTP_HOST=smtp.example.com
SMTP_PORT=587
SMTP_USERNAME=...
SMTP_PASSWORD=...
OTP_WHITELISTED_EMAILS=you@example.com
```

2. Start the stack:

```bash
make docker-restart   # rebuild + start app + Postgres
# or
docker compose up -d --build
```

3. Verify:

```bash
curl http://localhost:8080/api/v1/health-check
```

4. Explore the API — Swagger UI at `http://localhost:8080/swagger-ui.html` (basic auth; defaults `admin` / `admin`, override via `SWAGGER_USERNAME` / `SWAGGER_PASSWORD`).

### Run tests

```bash
make test-smoke        # full suite with AI stubs (no OPEN_AI_KEY)
make test-integration  # full suite against real OpenAI (requires .env.test with OPEN_AI_KEY)
```

### Export OpenAPI spec

```bash
make openapi   # requires a running API; writes openapi.json
```

## CI / CD

| Workflow | Trigger | What it does |
|----------|---------|-------------|
| **Smoke tests** | Pull request → `main` | Runs `make test-smoke` (required check — blocks merge on failure) |
| **Integration tests** | Push to `main`; manual (`workflow_dispatch`, branch default `main`) | Runs `make test-integration` against real OpenAI (`OPEN_AI_KEY` secret) |
| **Build, Test & Deploy** | Push to `main` | Smoke tests, then builds Docker image and deploys to Heroku |
| **Publish API Types** | Push to `main` when `openapi.json` changes | Generates TypeScript types and publishes to GitHub Packages |

## Testing

- **Suite entry point:** `com.ord.AllTestsSuite`
- **Pattern:** controller integration tests with `ControllerTestBase`, typed `*APIClient` wrappers, seeders/factories
- **Database:** real PostgreSQL via Testcontainers with Flyway migrations applied at suite start
- **Policy:** every new endpoint ships with integration tests registered in the suite

## Note

This is a personal / hobby project — not a commercial product — but it is intentionally built to production standards: reactive throughout, migration-managed schema, centralized error handling, and a maintained API contract.
