# AGENTS.md

See `CLAUDE.md` and `docs/ai-rules/` for coding conventions. General setup/run/test
commands are documented in `README.md` and the `Makefile`.

## Cursor Cloud specific instructions

The product is a single Kotlin / Spring Boot **WebFlux** REST API (`ord-api`) for a
language-learning app. It serves on port `8080` and depends on **PostgreSQL 16** (port `5432`).
There is no frontend in this repo; explore/test the API via Swagger UI at
`http://localhost:8080/swagger-ui.html` (HTTP basic auth, default `admin` / `admin`).

### Toolchain (already installed in the snapshot)
- **JDK 24** (Temurin, at `/usr/lib/jvm/jdk-24.0.2+12`) is the default `java`/`javac` and is what
  the build expects (`<java.version>24</java.version>`). `JAVA_HOME` is exported in `~/.bashrc`.
- **Maven** (`mvn`) is installed system-wide. There is no `mvnw` wrapper and there are no `.java`
  sources (pure Kotlin).
- **Docker** is installed but the daemon is **not auto-started**. Start it before running Postgres
  or the test suite: `sudo dockerd &` (runs with the `fuse-overlayfs` storage driver).

### Dev config files (`.env`, `.env.test`) — gitignored
Both are required and are **not committed** (only `.env.example` exists). They contain dev-only
dummy values and persist in the VM snapshot. If missing, recreate them:
- `.env` (used for the app run and by `docker compose`): set `DATABASE_URL=postgres://ord_user:ord_password@localhost:5432/ord_db`,
  `JWT_SECRET_KEY=<anything>`, `ENV_TEST_PROPERTY=1test1`, `OTP_WHITELISTED_EMAILS=dev@ord-api.com`,
  and the `DOCKER_DB_*` credentials.
- `.env.test` (sourced by `make test`): must set `SPRING_PROFILES_ACTIVE=test`, `ENV_TEST_PROPERTY=1test1`,
  `JWT_SECRET_KEY`, and `OPEN_AI_KEY` (any non-empty value).

### Running the app (dev mode)
1. `sudo dockerd &` (if not running).
2. Start Postgres: `docker start ord-db` (created once via
   `docker run -d --name ord-db -e POSTGRES_DB=ord_db -e POSTGRES_USER=ord_user -e POSTGRES_PASSWORD=ord_password -p 5432:5432 postgres:16.3`).
3. `set -a && source .env && set +a && mvn spring-boot:run` — Flyway applies migrations at startup.
   The app aborts on boot unless `ENV_TEST_PROPERTY=1test1` and the DB is reachable.
4. Verify: `curl http://localhost:8080/api/v1/health-check` → `{"application":"UP","database":"UP"}`.

Auth without SMTP: any email in `OTP_WHITELISTED_EMAILS` logs in with the fixed OTP code `123456`
(`POST /api/v1/auth/otp-request` then `/api/v1/auth/otp-verify`).

### Tests
- Run with `make test` (runs `com.ord.AllTestsSuite`; needs the Docker daemon — the suite starts
  its own PostgreSQL via **Testcontainers**). `.env.test` must set `SPRING_PROFILES_ACTIVE=test`.
- **Docker 29 gotcha:** Testcontainers' bundled `docker-java` negotiates an API version that
  Docker 29 rejects ("client version 1.32 is too old"). This is fixed by `~/.docker-java.properties`
  containing `api.version=1.43` (already present in the snapshot). Recreate that file if tests fail
  to find a valid Docker environment.
- **~60 tests require live third-party secrets and fail without them** (this is expected; CI injects
  them). The AI controller tests (conversations, word AI, AI explainer, AI games, QAW AI) call the
  **real OpenAI API** and return 401 with a dummy `OPEN_AI_KEY`; two auth tests need real **SMTP**.
  The `test` profile only mocks ElevenLabs TTS and fixes the OTP code — it does **not** mock OpenAI.
  All other ~482 tests pass offline.

### Lint / build
- There is no dedicated linter configured (no ktlint/detekt). Use `mvn -DskipTests compile` to
  check compilation.
- Build artifact: `mvn -DskipTests package` (or the multi-stage `Dockerfile` / `make docker-restart`).
