# Flyway migrations are append-only and versioned

Migrations live in `src/main/resources/db/migration/` and are named `V<n>__<snake_case_description>.sql` with a strictly increasing version number. Once a migration has been applied (anywhere beyond your local DB), it is immutable: never edit it. To change the schema, add a new `V<n+1>__...sql` file. Use `IF NOT EXISTS`, `TIMESTAMP WITH TIME ZONE` for timestamps, and `gen_random_uuid()` defaults for UUID primary keys, mirroring the existing files.

## Good

```sql
-- New file: V22__add_conversation_archived_flag.sql (next free version number)
ALTER TABLE conversations
    ADD COLUMN IF NOT EXISTS is_archived BOOLEAN NOT NULL DEFAULT FALSE;
```

## Bad

```sql
-- Editing the already-applied V17__create_conversation_tables.sql in place
-- to add a column. Flyway checksum changes -> migration validation fails.
CREATE TABLE IF NOT EXISTS conversations
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    is_archived BOOLEAN NOT NULL DEFAULT FALSE, -- <- do not modify a shipped migration
    topic       TEXT NOT NULL
);
```
