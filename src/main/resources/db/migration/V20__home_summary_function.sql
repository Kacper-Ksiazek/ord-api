CREATE OR REPLACE FUNCTION home_summary(
    p_user_id uuid,
    p_language text,
    p_from_month timestamptz,
    p_from_90 timestamptz,
    p_to_exclusive timestamptz,
    p_year_start timestamptz,
    p_year_end timestamptz
)
    RETURNS TABLE
            (
                words_total                     bigint,
                words_added_last_30             bigint,
                words_by_type                   text,
                conversations_total             bigint,
                conversations_created_last_30   bigint,
                messages_total                  bigint,
                messages_last_30                bigint,
                games_total                     bigint,
                games_last_30                   bigint,
                words_added_trend_90            text,
                conversations_created_trend_90  text,
                messages_trend_90               text,
                games_finished_trend_90         text,
                activity_days                   text
            )
    LANGUAGE sql
    STABLE
AS
$$
SELECT (
           SELECT COUNT(*)
           FROM words
           WHERE user_id = p_user_id
             AND language = p_language
       ),
       (
           SELECT COUNT(*)
           FROM words
           WHERE user_id = p_user_id
             AND language = p_language
             AND created_at >= p_from_month
             AND created_at < p_to_exclusive
       ),
       (
           SELECT COALESCE(jsonb_object_agg(grouped.type_name, grouped.cnt), '{}'::jsonb)::text
           FROM (
                    SELECT type::text AS type_name, COUNT(*) AS cnt
                    FROM words
                    WHERE user_id = p_user_id
                      AND language = p_language
                    GROUP BY type
                    HAVING COUNT(*) > 0
                ) grouped
       ),
       (
           SELECT COUNT(*)
           FROM conversations
           WHERE user_id = p_user_id
             AND language = p_language
       ),
       (
           SELECT COUNT(*)
           FROM conversations
           WHERE user_id = p_user_id
             AND language = p_language
             AND created_at >= p_from_month
             AND created_at < p_to_exclusive
       ),
       (
           SELECT COUNT(*)
           FROM conversation_messages cm
                    JOIN conversations c ON c.id = cm.conversation_id
           WHERE c.user_id = p_user_id
             AND c.language = p_language
       ),
       (
           SELECT COUNT(*)
           FROM conversation_messages cm
                    JOIN conversations c ON c.id = cm.conversation_id
           WHERE c.user_id = p_user_id
             AND c.language = p_language
             AND cm.created_at >= p_from_month
             AND cm.created_at < p_to_exclusive
       ),
       (
           SELECT COUNT(*)
           FROM finished_games
           WHERE user_id = p_user_id
             AND language = p_language
       ),
       (
           SELECT COUNT(*)
           FROM finished_games
           WHERE user_id = p_user_id
             AND language = p_language
             AND created_at >= p_from_month
             AND created_at < p_to_exclusive
       ),
       (
           SELECT COALESCE(
                          jsonb_agg(
                          jsonb_build_object(
                                  'date', to_char(daily.activity_date, 'YYYY-MM-DD'),
                                  'count', daily.cnt
                          )
                          ORDER BY daily.activity_date
                                   ),
                          '[]'::jsonb
                  )::text
           FROM (
                    SELECT (created_at AT TIME ZONE 'UTC')::date AS activity_date, COUNT(*)::bigint AS cnt
                    FROM words
                    WHERE user_id = p_user_id
                      AND language = p_language
                      AND created_at >= p_from_90
                      AND created_at < p_to_exclusive
                    GROUP BY 1
                ) daily
       ),
       (
           SELECT COALESCE(
                          jsonb_agg(
                          jsonb_build_object(
                                  'date', to_char(daily.activity_date, 'YYYY-MM-DD'),
                                  'count', daily.cnt
                          )
                          ORDER BY daily.activity_date
                                   ),
                          '[]'::jsonb
                  )::text
           FROM (
                    SELECT (created_at AT TIME ZONE 'UTC')::date AS activity_date, COUNT(*)::bigint AS cnt
                    FROM conversations
                    WHERE user_id = p_user_id
                      AND language = p_language
                      AND created_at >= p_from_90
                      AND created_at < p_to_exclusive
                    GROUP BY 1
                ) daily
       ),
       (
           SELECT COALESCE(
                          jsonb_agg(
                          jsonb_build_object(
                                  'date', to_char(daily.activity_date, 'YYYY-MM-DD'),
                                  'count', daily.cnt
                          )
                          ORDER BY daily.activity_date
                                   ),
                          '[]'::jsonb
                  )::text
           FROM (
                    SELECT (cm.created_at AT TIME ZONE 'UTC')::date AS activity_date, COUNT(*)::bigint AS cnt
                    FROM conversation_messages cm
                             JOIN conversations c ON c.id = cm.conversation_id
                    WHERE c.user_id = p_user_id
                      AND c.language = p_language
                      AND cm.created_at >= p_from_90
                      AND cm.created_at < p_to_exclusive
                    GROUP BY 1
                ) daily
       ),
       (
           SELECT COALESCE(
                          jsonb_agg(
                          jsonb_build_object(
                                  'date', to_char(daily.activity_date, 'YYYY-MM-DD'),
                                  'count', daily.cnt
                          )
                          ORDER BY daily.activity_date
                                   ),
                          '[]'::jsonb
                  )::text
           FROM (
                    SELECT (created_at AT TIME ZONE 'UTC')::date AS activity_date, COUNT(*)::bigint AS cnt
                    FROM finished_games
                    WHERE user_id = p_user_id
                      AND language = p_language
                      AND created_at >= p_from_90
                      AND created_at < p_to_exclusive
                    GROUP BY 1
                ) daily
       ),
       (
           SELECT COALESCE(
                          jsonb_agg(
                          jsonb_build_object(
                                  'date', to_char(daily.activity_date, 'YYYY-MM-DD'),
                                  'count', daily.cnt
                          )
                          ORDER BY daily.activity_date
                                   ),
                          '[]'::jsonb
                  )::text
           FROM (
                    SELECT activity_date, SUM(cnt) AS cnt
                    FROM (
                             SELECT (created_at AT TIME ZONE 'UTC')::date AS activity_date, COUNT(*) AS cnt
                             FROM words
                             WHERE user_id = p_user_id
                               AND language = p_language
                               AND created_at >= p_year_start
                               AND created_at < p_year_end
                             GROUP BY 1
                             UNION ALL
                             SELECT (cm.created_at AT TIME ZONE 'UTC')::date, COUNT(*)
                             FROM conversation_messages cm
                                      JOIN conversations c ON c.id = cm.conversation_id
                             WHERE c.user_id = p_user_id
                               AND c.language = p_language
                               AND cm.created_at >= p_year_start
                               AND cm.created_at < p_year_end
                             GROUP BY 1
                             UNION ALL
                             SELECT (created_at AT TIME ZONE 'UTC')::date, COUNT(*)
                             FROM finished_games
                             WHERE user_id = p_user_id
                               AND language = p_language
                               AND created_at >= p_year_start
                               AND created_at < p_year_end
                             GROUP BY 1
                         ) events
                    GROUP BY activity_date
                    HAVING SUM(cnt) > 0
                ) daily
       );
$$;
