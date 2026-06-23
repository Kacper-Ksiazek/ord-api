# snake_case columns map to camelCase entity fields

Database columns are `snake_case`; Kotlin entity properties are `camelCase`. Spring Data R2DBC maps them automatically for derived methods, so do not annotate every field. In hand-written SQL you must use the real `snake_case` column names in the query and when reading from the row map (`row["ai_interlocutor_name"]`), while the constructed object uses the `camelCase` property.

## Good

```sql
ai_interlocutor_name      TEXT                 NOT NULL,
ai_interlocutor_avatar_id VARCHAR(64)          NOT NULL,
proficiency_level         language_proficiency NOT NULL,
```

```kotlin
ConversationEntity(
    aiInterlocutorName = row["ai_interlocutor_name"] as String,
    aiInterlocutorAvatarId = row["ai_interlocutor_avatar_id"] as String,
    proficiencyLevel = LanguageProficiencyLevel.valueOf(row["proficiency_level"] as String),
)
```

## Bad

```kotlin
// Reading by the camelCase property name -> column does not exist, value is null.
ConversationEntity(
    aiInterlocutorName = row["aiInterlocutorName"] as String,
    proficiencyLevel = row["proficiencyLevel"] as String,
)
```
