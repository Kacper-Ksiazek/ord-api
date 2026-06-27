# core/ vs features/ vs shared/

Place a module by its role: `core/` holds foundational domain concerns the rest of the app depends on (`auth`, `user`, `word`, `langugae_proficiency`, `gpt_tokens_usage`, `security`, `ai_provider`); `features/` holds self-contained product features (`conversation`, `game`, `bank`, `ai_explainer`, ...); `shared/` holds generic, domain-agnostic infrastructure (base interfaces, mappers, utils, extensions, prompts) with no business logic. `shared/` must never depend on `core/` or `features/`.

## Good

```kotlin
// Generic base type with no domain knowledge -> shared/
package com.ord.shared.models

interface IdentifiableUserResource {
    val id: UUID?
    val userId: UUID
}

// A concrete domain entity that uses it -> core/ (foundational) or features/
package com.ord.core.word.models.word

@Table("words")
data class WordEntity(
    @Id override val id: UUID? = null,
    override var userId: UUID,
    var sourceWord: String,
) : IdentifiableUserResource
```

## Bad

```kotlin
// WRONG: shared/ must stay domain-agnostic; it cannot import a feature type
package com.ord.shared.models

import com.ord.features.conversation.models.conversation.ConversationEntity // shared -> features dependency

class ConversationHelper { /* ... */ }
```
