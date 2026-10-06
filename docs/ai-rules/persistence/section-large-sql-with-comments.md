# Section large SQL with comments

A SQL string that mixes several aggregates, trends, or `UNION` branches gets seed-style section comments so the block can be scanned without reading every line. Each banner line is a SQL comment (`--`), including the dashed rules. A single short `SELECT` does not need a banner.

## Good

```sql
-- -------------
-- 1. words
-- Lifetime total, the current UTC month, and counts by type.
-- -------------
SELECT COUNT(*)
FROM words
WHERE user_id = :userId
  AND language = :language
```

```sql
-- -------------
-- Year heatmap
-- Words, messages, and finished games collapse into one count per UTC day.
-- -------------
SELECT to_char(daily.activity_date, 'YYYY-MM-DD') AS activity_date, daily.cnt
FROM (
         -- words
         SELECT (created_at AT TIME ZONE 'UTC')::date AS activity_date, COUNT(*) AS cnt
         FROM words
         -- ...
         UNION ALL
         -- messages
         SELECT (cm.created_at AT TIME ZONE 'UTC')::date, COUNT(*)
         FROM conversation_messages cm
         -- ...
     ) daily
```

## Bad

```sql
SELECT (SELECT COUNT(*) FROM words WHERE user_id = :userId) AS words_total,
       (SELECT COUNT(*) FROM conversations WHERE user_id = :userId) AS conversations_total,
       (SELECT COUNT(*) FROM finished_games WHERE user_id = :userId) AS games_total
-- two hundred more lines of counts and trends, with no section marker
```

```sql
-------------
-- 1. words
-------------
```

The dashed lines are not comments. PostgreSQL tries to parse them.
