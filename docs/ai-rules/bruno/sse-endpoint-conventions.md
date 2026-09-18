# Mark SSE endpoints explicitly

Streaming endpoints (`produces = TEXT_EVENT_STREAM`) set `Accept: text/event-stream` and suffix the file name with `-sse`. Document that Bruno may not fully render the stream and that `OPEN_AI_KEY` must be configured on the API.

## Good

```bru
meta { name: AI Initialize Conversation (SSE) }

docs { SSE stream. Requires conversationId from create. }

post {
  url: {{baseUrl}}{{apiPrefix}}/conversations/ongoing/ai/initialize
  auth: inherit
}

headers {
  Accept: text/event-stream
}

params:query {
  conversationId: {{conversationId}}
}
```

## Bad

```bru
post {
  url: {{baseUrl}}{{apiPrefix}}/conversations/ongoing/ai/initialize
  auth: inherit
}
# missing Accept header and -sse file name convention
```
