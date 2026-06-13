# ORD API — Project Brief

## What it is

ORD is the backend API for an **AI-powered language / vocabulary learning app**. Users learn
words in a foreign language and practice them through several interactive features:

- **Words & word details** — managing vocabulary the user is learning, with AI-generated details.
- **Banks & bank groups** — organizing words into collections.
- **Quickly added words (QAW)** — a fast capture flow for new vocabulary, including a public
  (anonymous) entry point.
- **Games** — practice variants (crossword, sentences writing, words typing) generated and
  reviewed by AI.
- **Conversations** — AI-powered conversation practice with customizable scenarios,
  interlocutors, tones, plus per-message analysis and learning tips.
- **AI explainer** — on-demand AI explanations.
- **Language proficiencies** — each user declares languages and CEFR levels (A1–C2); most
  generative features are gated on the user's proficiency for the target language.

Everything is **user-scoped**: data belongs to an authenticated user, and authentication is
**OTP-based** (email one-time codes) issuing a JWT delivered via cookie.

## Goal & nature

- **Hobby / learning project.** Not (currently) a commercial product, but built with
  production-grade patterns (strict layering, full integration test coverage, OpenAPI spec).
- The priority for any contributor — human or AI — is **strict consistency with the existing
  layered architecture** and **maintaining test coverage** for new endpoints.

## Who consumes the API

- The developer's **own frontend** (a TypeScript client). API types are generated from the
  OpenAPI spec and published as an npm-style `types-package`. This means:
  - The OpenAPI spec (`openapi.json`) and Swagger annotations are a real contract — keep them
    accurate.
  - Breaking changes to request/response DTOs ripple into the frontend type package.

## What an AI agent should know about intent

- Favor **consistency over cleverness**: new features should mirror an existing comparable
  feature (e.g. copy the shape of the `conversation` or `word` feature) rather than introduce
  new patterns.
- The stack is **fully reactive (WebFlux + R2DBC)** — never block the event loop.
- New endpoints should ship with **controller integration tests** (Kotest + Testcontainers)
  following the established `*APIClient` + `ControllerTestBase` pattern.
- AI/OpenAI calls are real and cost tokens; token usage is logged per operation. Be deliberate.
- **Never commit on the agent's own initiative** — the repo requires explicit human approval
  before any commit (see git rules / `.clinerules`).
