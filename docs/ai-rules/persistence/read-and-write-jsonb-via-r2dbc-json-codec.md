# Read and write JSONB columns via io.r2dbc.postgresql.codec.Json

`JSONB` columns are exchanged as `io.r2dbc.postgresql.codec.Json`. When reading, cast the row value to `Json` and hand it to the dedicated mapper's deserialize method; do not cast it to `String` or attempt to parse it inline. Keep JSON (de)serialization in the feature's mapper, not in the repository row-mapping block.

## Good

```kotlin
import io.r2dbc.postgresql.codec.Json

ConversationUserMessageAnalysisDTO(
    mistakes = analysisMapper.deserializeMistakes(row["analysis_mistakes"] as Json),
    strengths = analysisMapper.deserializeStrengths(row["analysis_strengths"] as Json),
    suggestions = analysisMapper.deserializeSuggestions(row["analysis_suggestions"] as Json),
    // ...
)
```

## Bad

```kotlin
// JSONB does not arrive as String, and parsing JSON inside the repository
// scatters serialization logic that belongs in the mapper.
ConversationUserMessageAnalysisDTO(
    mistakes = objectMapper.readValue(row["analysis_mistakes"] as String),
)
```
