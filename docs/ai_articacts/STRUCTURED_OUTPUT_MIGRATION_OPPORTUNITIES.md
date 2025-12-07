# Structured Output Migration Opportunities

This document provides a comprehensive analysis of remaining opportunities to migrate AI endpoints to OpenAI structured outputs for improved reliability and type safety.

## Migration Status Overview

### ✅ Already Migrated (6 endpoints)

| Feature | Prompt | Status |
|---------|--------|--------|
| **Conversation Review** | `CONVERSATION_REVIEW_USER_RESPONSE` | ✅ Migrated |
| **Word Manual Generation** | `WORDS_GENERATE_MANUAL` | ✅ Migrated |
| **Sentences Writing Review** | `GAMES_REVIEW_SENTENCES_WRITING` | ✅ Migrated |
| **Crossword Generation** | `GAMES_GENERATE_CROSSWORD` | ✅ Migrated |
| **Words Typing Generation** | `GAMES_GENERATE_WORDS_TYPING` | ✅ Migrated |
| **Word Manual** | `WORDS_GENERATE_MANUAL` | ✅ Migrated |

---

## 🎯 High Priority Migration Candidates

These endpoints have well-defined structured outputs and would benefit most from schema enforcement.

### 1. Sentences Writing Game Generation

**Prompt:** `GAMES_GENERATE_SENTENCES_WRITING`
**File:** `src/main/resources/prompts/games/generate_sentences_writing_game.md`
**Service:** `SentencesWritingAIGenerateService`
**Current Response Type:** `AIGeneratedSentencesWritingGame` (typealias for `Map<String, String>`)

**Current Format:**
```typescript
Map<String, String>
// Keys: words, Values: topics
```

**Migration Effort:** 🟢 Low
**Benefit:** High - Ensures all words have corresponding topics, validates map structure

**Recommended Schema:**
```kotlin
{
  "type": "object",
  "properties": {
    "wordTopics": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "word": { "type": "string" },
          "topic": { "type": "string" }
        },
        "required": ["word", "topic"]
      }
    }
  },
  "required": ["wordTopics"]
}
```

**Pattern:** Similar to Words Typing game - wrap map in array of objects.

---

### 2. Vocabulary Suggestions

**Prompt:** `WORDS_SUGGEST_VOCABULARY`
**File:** `src/main/resources/prompts/words/suggest_vocabulary.md`
**Service:** `WordAIFacadeImpl.suggestVocabulary()`
**Current Response Type:** Streaming with `VocabularySuggestion` objects separated by `STREAMING_CONTENT_SEPARATOR`

**Current Format:**
```typescript
interface Suggestion {
  word: string
  translation: string
  definition: string
}
```

**Migration Effort:** 🟡 Medium
**Benefit:** Very High - Eliminates separator parsing, ensures all fields present, validates structure

**Recommended Schema:**
```kotlin
{
  "type": "object",
  "properties": {
    "suggestions": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "word": { "type": "string" },
          "translation": { "type": "string" },
          "definition": { "type": "string" }
        },
        "required": ["word", "translation", "definition"]
      }
    }
  },
  "required": ["suggestions"]
}
```

**Considerations:**
- Currently uses streaming with separator - would need to change to batch response
- Could significantly improve reliability (no more separator parsing failures)
- May need UX adjustment for non-streaming response

---

### 3. AI Interlocutor Generation

**Prompt:** `CONVERSATION_GENERATE_AI_INTERLOCUTOR`
**File:** `src/main/resources/prompts/conversation/generate_ai_interlocutor.md`
**Service:** `ConversationAuxiliaryFacadeImpl.generateAIInterlocutorData()`
**Current Response Type:** `GeneratedAIInterlocutorData`

**Current Format:**
```typescript
{
  name: string
  avatarId: string
}
```

**Migration Effort:** 🟢 Low
**Benefit:** Medium - Simple schema, ensures avatar ID validation, guarantees both fields present

**Recommended Schema:**
```kotlin
{
  "type": "object",
  "properties": {
    "name": {
      "type": "string",
      "description": "Full name of the AI interlocutor"
    },
    "avatarId": {
      "type": "string",
      "description": "Avatar ID from the provided list"
    }
  },
  "required": ["name", "avatarId"],
  "additionalProperties": false
}
```

**Pattern:** Straightforward object schema, already well-structured in prompt.

---

### 4. Conversation Topic Suggestions

**Prompt:** `CONVERSATION_SUGGEST_TOPIC`
**File:** `src/main/resources/prompts/conversation/suggest_conversation_topic.md`
**Service:** `ConversationAuxiliaryFacadeImpl.suggestTopics()`
**Current Response Type:** Streaming with `StreamSimpleItem` objects separated by `STREAMING_CONTENT_SEPARATOR`

**Current Format:**
```typescript
{"value": "topic 1"}
**SEPARATOR**
{"value": "topic 2"}
**SEPARATOR**
{"value": "topic 3"}
```

**Migration Effort:** 🟡 Medium
**Benefit:** High - Eliminates separator parsing, ensures exactly 3 topics returned

**Recommended Schema:**
```kotlin
{
  "type": "object",
  "properties": {
    "topics": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "value": { "type": "string" }
        },
        "required": ["value"]
      },
      "minItems": 3,
      "maxItems": 3
    }
  },
  "required": ["topics"]
}
```

**Considerations:**
- Currently uses streaming with separator
- Could use `minItems`/`maxItems` to enforce exactly 3 topics
- Would need to change from streaming to batch response

---

## 🔵 Lower Priority / Special Cases

These endpoints are less suitable for structured outputs due to their nature.

### 5. Word/Phrase Explanation

**Prompt:** `WORDS_EXPLAIN`
**File:** `src/main/resources/prompts/words/explain_word.md`
**Service:** `AIExplainerFacadeImpl.explainPhrase()`
**Current Response Type:** Plain text stream

**Current Format:** Free-form text explanation

**Migration Effort:** ❌ Not Recommended
**Benefit:** Low - Plain text format is intentional for natural explanations

**Reason to Skip:**
- Explicitly requires plain text output with no formatting
- Designed for streaming natural language
- Structured output would constrain the educational explanation quality
- Current approach is optimal for this use case

---

### 6. Conversation AI Response

**Prompt:** `CONVERSATION_REQUEST_AI_RESPONSE`
**File:** `src/main/resources/prompts/conversation/respond_in_conversation.md`
**Service:** Conversation service (streaming)

**Current Format:** Plain text conversation response

**Migration Effort:** ❌ Not Recommended
**Benefit:** None - Natural conversation requires free-form text

**Reason to Skip:**
- Natural conversation responses should remain unstructured
- Streaming is essential for good UX
- Any schema would artificially constrain AI responses

---

### 7. Conversation Initialization

**Prompt:** `CONVERSATION_INITIALIZE`
**File:** `src/main/resources/prompts/conversation/initialize_conversation.md`
**Service:** Conversation service (streaming)

**Current Format:** Initial conversation message (plain text)

**Migration Effort:** ❌ Not Recommended
**Benefit:** None - Same as conversation response

**Reason to Skip:**
- Natural conversation starter
- No structure to enforce

---

## Migration Priority Ranking

| Rank | Feature | Effort | Benefit | Priority Score |
|------|---------|--------|---------|----------------|
| 1 | **Vocabulary Suggestions** | Medium | Very High | ⭐⭐⭐⭐⭐ |
| 2 | **Sentences Writing Generation** | Low | High | ⭐⭐⭐⭐ |
| 3 | **Topic Suggestions** | Medium | High | ⭐⭐⭐⭐ |
| 4 | **AI Interlocutor** | Low | Medium | ⭐⭐⭐ |
| - | Word Explanation | - | - | ❌ Skip |
| - | Conversation Response | - | - | ❌ Skip |
| - | Conversation Initialize | - | - | ❌ Skip |

---

## Implementation Considerations

### For Streaming Endpoints

**Current Pattern:**
- Uses `STREAMING_CONTENT_SEPARATOR` to split streamed JSON objects
- Parses each chunk individually
- Good for progressive UI updates

**Structured Output Pattern:**
- Returns complete array in single response
- No separator parsing needed
- More reliable but loses progressive streaming

**Decision Points:**
1. **Keep Streaming:** If progressive UI updates are critical
2. **Switch to Batch:** If reliability > progressive updates

### For Map-Based Responses

**Pattern to Follow:**
- Wrap maps in objects with array of key-value pairs
- Example: `Map<String, String>` → `{ wordPairs: [{ word, value }] }`
- Already proven with Words Typing game migration

---

## Recommended Migration Order

1. **Phase 1: Low-Hanging Fruit**
   - AI Interlocutor Generation (simple object)
   - Sentences Writing Generation (similar to existing pattern)

2. **Phase 2: High-Value Conversions**
   - Vocabulary Suggestions (high reliability gain)
   - Topic Suggestions (high reliability gain)

3. **Phase 3: Evaluation**
   - Assess impact of Phase 1-2 migrations
   - Decide if streaming → batch tradeoff is acceptable

---

## Migration Checklist Template

For each endpoint to migrate:

- [ ] Create JSON schema in `structured_outputs/features/{domain}/`
- [ ] Create OpenAI intermediate DTOs in `{domain}/api/ai/responses/openai/`
- [ ] Update service to use OpenAI DTO + `toDomain()`
- [ ] Remove TypeScript format from prompt file
- [ ] Register schema in `AvailablePrompts`
- [ ] Test with actual API calls
- [ ] Create atomic commits
- [ ] Update this document to mark as completed

---

## Summary

**Total Endpoints Analyzed:** 13
**Already Migrated:** 6 ✅
**High Priority Candidates:** 4 🎯
**Not Recommended:** 3 ❌

**Next Best Candidates for Migration:**
1. Vocabulary Suggestions
2. Sentences Writing Generation
3. Topic Suggestions
4. AI Interlocutor Generation
