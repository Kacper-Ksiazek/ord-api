# Foreign keys cascade on delete and hot lookups are indexed

Child tables reference their parent with `FOREIGN KEY (...) REFERENCES parent (id) ON DELETE CASCADE` so deleting a parent (e.g. a user or conversation) cleans up dependents automatically. Add `CREATE INDEX IF NOT EXISTS` for the columns you actually filter and order by in queries — typically the FK plus the timestamp used in `ORDER BY` (e.g. `(user_id, created_at)`), matching the access patterns in the custom repository SQL.

## Good

```sql
CREATE TABLE IF NOT EXISTS conversation_messages
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_conversations_user_id_created_at
    ON conversations (user_id, created_at);
```

## Bad

```sql
-- No ON DELETE CASCADE (orphan rows on parent delete) and no index on the
-- (user_id, created_at) pair that every conversation query filters/sorts by.
CREATE TABLE IF NOT EXISTS conversation_messages
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL,

    FOREIGN KEY (conversation_id) REFERENCES conversations (id)
);
```
