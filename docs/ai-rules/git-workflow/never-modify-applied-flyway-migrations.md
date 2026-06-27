# Never modify an already-applied Flyway migration

Flyway migrations in `src/main/resources/db/migration/` are append-only. Never edit, rename, renumber, or delete an existing `V<n>__*.sql` file (the latest is `V21__seed_root_user_conversation.sql`) — changing applied migrations breaks Flyway checksums and the deploy. To change the schema, add a new sequential migration.

## Good

```bash
# Add a new migration with the next version number.
cat > src/main/resources/db/migration/V22__add_streak_to_users.sql <<'SQL'
ALTER TABLE users ADD COLUMN streak_days INT NOT NULL DEFAULT 0;
SQL
```

## Bad

```bash
# Editing an already-applied migration in place — breaks Flyway checksum validation.
echo "ALTER TABLE users ADD COLUMN streak_days INT;" \
  >> src/main/resources/db/migration/V04__create_users_table.sql
# Or renaming an applied migration:
git mv src/main/resources/db/migration/V21__seed_root_user_conversation.sql \
       src/main/resources/db/migration/V21__seed_root_conversation.sql
```
