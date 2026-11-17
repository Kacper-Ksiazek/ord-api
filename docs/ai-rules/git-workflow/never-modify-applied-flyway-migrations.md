# Never modify an already-applied Flyway migration

Flyway migrations in `src/main/resources/db/migration/` are append-only. Never edit, rename, renumber, or delete an existing `V<n>__*.sql` file (the latest is `V15__seed_data.sql`) — changing applied migrations breaks Flyway checksums and the deploy. To change the schema, add a new sequential migration.

## Good

```bash
# Add a new migration with the next version number.
cat > src/main/resources/db/migration/V16__add_streak_to_users.sql <<'SQL'
ALTER TABLE users ADD COLUMN streak_days INT NOT NULL DEFAULT 0;
SQL
```

## Bad

```bash
# Editing an already-applied migration in place — breaks Flyway checksum validation.
echo "ALTER TABLE users ADD COLUMN streak_days INT;" \
  >> src/main/resources/db/migration/V04__create_users_table.sql
# Or renaming an applied migration:
git mv src/main/resources/db/migration/V13__create_conversation_tables.sql \
       src/main/resources/db/migration/V13__create_conversations.sql
```

## Seed data

All seed data (root user, vocabulary with manuals, sample conversation, E2E worker users) lives in a single file, `V15__seed_data.sql`. Do not add patch migrations to fix seed content; editing that file requires resetting the database and its Flyway history, so coordinate it with the maintainer first.
