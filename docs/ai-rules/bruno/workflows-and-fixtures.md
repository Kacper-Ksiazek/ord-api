# Keep multi-step flows in workflows/ and shared bodies in _fixtures/

Cross-domain sequences (onboarding, conversation session, word lifecycle) live in `workflows/` as documented runbooks — not duplicated inside domain folders. Reusable JSON bodies go in `_fixtures/bodies/`.

## Good

```
workflows/
  conversation-session.bru    # docs-only runbook listing ordered steps
_fixtures/bodies/
  create-conversation.json
```

```bru
meta {
  name: Conversation Session
  type: http
  seq: 1
}

docs {
  Run in order:
  1. conversations/auxiliary/suggest-ai-interlocutor (optional)
  2. conversations/crud/create → sets conversationId
  3. conversations/ongoing/ai-initialize-sse (SSE)
  ...
}
```

## Bad

```
conversations/
  full-session-workflow.bru   # multi-step runbook buried inside a domain folder
```
