# Project only fields the screen renders

An aggregate or landing read that embeds another resource does not reuse that resource's list DTO. Define a dedicated type and select only the columns that type contains.

## Good

```kotlin
data class HomeRecentWord(
    val id: UUID,
    val sourceWord: String,
    val translation: String,
    val definitionPreview: String?,
    val isBookmarked: Boolean,
    val type: WordType,
    val extraMark: WordExtraMark?,
)
```

```sql
SELECT id, source_word, translation, left(definition, 160) AS definition_preview,
       is_bookmarked, type, extra_mark
FROM words
WHERE user_id = :userId AND language = :language
ORDER BY created_at DESC, id DESC
LIMIT :limit
```

## Bad

```kotlin
val recentWords: List<WordListItem>
```

The inbox query joins progress and banks, then the home screen ignores those fields.
