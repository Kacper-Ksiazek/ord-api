# Never modify an already-applied Flyway migration

Flyway migrations in `src/main/resources/db/migration/` are append-only. Never edit, rename, renumber, or delete an existing `V<n>__*.sql` file (the latest is `V19__seed_e2e_worker_users.sql`) — changing applied migrations breaks Flyway checksums and the deploy. To change the schema, add a new sequential migration.

## Good

```bash
# Add a new migration with the next version number.
cat > src/main/resources/db/migration/V20__add_streak_to_users.sql <<'SQL'
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

Seed data is split into one file per category so you only load the one you need:

| File | Content |
|------|---------|
| `V15__seed_root_user.sql` | root user account, language proficiencies, bank groups, banks |
| `V16__seed_words.sql` | root user vocabulary and its `word_progress` |
| `V17__seed_word_details.sql` | word manuals (`word_details`) |
| `V18__seed_root_user_conversation.sql` | sample conversation |
| `V19__seed_e2e_worker_users.sql` | E2E worker users |

Fix or extend seed content in the matching file instead of adding patch migrations. Editing a seed file requires resetting the database and its Flyway history, so coordinate it with the maintainer first.
