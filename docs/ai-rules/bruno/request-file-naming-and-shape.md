# Name request files after the HTTP operation

One `.bru` file per endpoint. File name = kebab-case summary of the operation (`create.bru`, `suggest-topics-sse.bru`, `ai-initialize-sse.bru`). Each file includes: `meta` (name, type, seq), optional `docs`, HTTP block with `{{baseUrl}}{{apiPrefix}}/…`, and a minimal `script:post-response` test.

## Good

```bru
meta {
  name: Create Conversation
  type: http
  seq: 3
}

post {
  url: {{baseUrl}}{{apiPrefix}}/conversations/
  body: json
  auth: inherit
}

script:post-response {
  test("created", () => expect(res.getStatus()).to.equal(200));
  const body = res.getBody();
  if (body?.id) bru.setEnvVar("conversationId", body.id);
}
```

## Bad

```bru
meta { name: POST conversations }
post { url: http://localhost:8080/api/v1/conversations/ }  # hardcoded URL, no tests
```
