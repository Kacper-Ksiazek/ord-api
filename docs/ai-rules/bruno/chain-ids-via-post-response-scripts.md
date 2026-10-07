# Chain IDs through environment variables

When a response produces an ID needed by later requests, capture it in `script:post-response` via `bru.setEnvVar`. Use descriptive names: `conversationId`, `wordId`, `gameId`. Document the chain on the producing request and on any workflow runbook.

## Good

```javascript
const body = res.getBody();
if (body?.id) bru.setEnvVar("conversationId", body.id);
```

Referenced downstream:

```bru
params:query {
  conversationId: {{conversationId}}
}
```

## Bad

```javascript
// Hardcode a UUID in every downstream request instead of capturing from create
```

```bru
params:query {
  conversationId: 650e8400-e29b-41d4-a716-446655440000
}
```
