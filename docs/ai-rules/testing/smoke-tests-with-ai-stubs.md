# Smoke tests with AI stubs

Controller integration tests that call OpenAI must not require a live API key in CI. Use the
test-only `StubOpenAIAPIClientService`, activated via `@Import(StubOpenAITestConfiguration::class)`
on [`TestcontainersConfig`](../../../src/test/kotlin/com/ord/controllers/bases/TestcontainersConfig.kt).

## How it works

- Production code is unchanged — stub lives only under `src/test`.
- `StubOpenAITestConfiguration` registers `@Primary OpenAIAPIClientService` →
  `StubOpenAIAPIClientService`.
- Responses come from JSON fixtures in `src/test/resources/mocks/ai/openai/` or from
  `AIFixtureDynamicBuilder` when the response must match prompt input (games, QAW, word manual).
- Fixtures are keyed by `gptTokensUsageLogKey` (`GptTokensUsageOperationType` constants).
- Token usage is still persisted so positive tests keep using `assertGptTokensLogCreated`.

## Running tests

| Command | Behaviour |
|---------|-----------|
| `make test` / `make test-smoke` | Full `AllTestsSuite` with AI stubs (no `OPEN_AI_KEY`) |
| `make test-integration` | Full suite against real OpenAI (`INTEGRATION_TESTS=true`, requires `.env.test` with `OPEN_AI_KEY`) |

CI uses `make test-smoke` and does not pass `OPEN_AI_KEY`.

## Adding a fixture for a new AI operation

1. Add a stable key in `GptTokensUsageOperationType` (if not already present).
2. Register the operation in [`AIFixtureRegistry`](../../../src/test/kotlin/com/ord/testing_utils/mocks/ai/AIFixtureRegistry.kt)
   with `STRUCTURED`, `STRING_STREAM`, or `ARRAY_STREAM`.
3. If the response is static, add JSON under `src/test/resources/mocks/ai/openai/<KEY>.json`
   or `<KEY>.stream.json`.
4. If the response must mirror words/IDs from the prompt (e.g. game generation), implement
   logic in [`AIFixtureDynamicBuilder`](../../../src/test/kotlin/com/ord/testing_utils/mocks/ai/AIFixtureDynamicBuilder.kt)
   and mark the registry entry `isDynamic = true`.
5. Run `make test-smoke` — existing controller tests should pass without edits.

## Fixture formats

**Structured (`makeRequest`)** — body matching the OpenAI DTO, e.g.:

```json
{
  "name": "Alex",
  "avatarId": "AVATAR_DEFAULT"
}
```

**String stream** (`openSimpleStringStream`):

```json
{
  "chunks": ["Hello ", "world"],
  "inputTokens": 42,
  "outputTokens": 18
}
```

**Array stream** (`openStructuredArrayStream`):

```json
{
  "items": [
    { "value": "Topic one" },
    { "value": "Topic two" }
  ],
  "inputTokens": 55,
  "outputTokens": 22
}
```

## Good

- New AI endpoint ships with a registry entry + fixture; smoke suite passes without API cost.
- Dynamic builder parses words/IDs from the prompt when validation depends on request context.

## Bad

- Calling OpenAI directly from tests or requiring `OPEN_AI_KEY` in CI for the default suite.
- Hardcoding AI responses inside production `OpenAIAPIClientServiceImpl`.
