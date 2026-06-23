# Convert timestamp columns from OffsetDateTime to Instant

`TIMESTAMP WITH TIME ZONE` columns come back from R2DBC as `java.time.OffsetDateTime`, but the domain models store `java.time.Instant`. When mapping rows, cast the column to `OffsetDateTime` and call `.toInstant()`. Never cast a timestamp column directly to `Instant`.

## Good

```kotlin
createdAt = (row["created_at"] as OffsetDateTime).toInstant(),
updatedAt = (row["updated_at"] as OffsetDateTime).toInstant(),
createdAt = (row["message_created_at"] as OffsetDateTime).toInstant()
```

## Bad

```kotlin
// OffsetDateTime cannot be cast to Instant directly -> ClassCastException at runtime.
createdAt = row["created_at"] as Instant,
updatedAt = row["updated_at"] as Instant
```
