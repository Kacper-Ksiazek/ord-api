# Be token-cost aware when building AI requests

Every AI call costs input + output tokens that get logged per user. Keep prompts lean: bound any list you interpolate into a prompt (the vocabulary suggester caps existing words, recent topics/interlocutors are limited to ~10), and don't dump unbounded user history or whole tables into the prompt. Make one AI request per logical operation and reuse its result — never loop a `makeRequest` to "fix" output you could have validated, and don't issue extra calls just to reformat data you already have.

## Good

```kotlin
// Bounded inputs keep the prompt (and token cost) predictable.
val recentTopicsFromDB = conversationService.findRecentTopics(
    userId = userId,
    language = body.language,
    limit = 10,
    type = body.conversationType
).collectList()

val existingWordsString =
    if (allExistingWords.isEmpty()) "No existing vocabulary"
    else allExistingWords.joinToString(", ")
```

## Bad

```kotlin
// Unbounded history bloats the prompt and token usage on every call.
val allMessages = conversationService.findAllMessages(userId).collectList().block()!!
val prompt = "Here is the user's entire history:\n" +
    allMessages.joinToString("\n") { it.content } +
    "\nNow suggest topics."
```
