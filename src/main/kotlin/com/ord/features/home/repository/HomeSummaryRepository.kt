package com.ord.features.home.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.home.model.HomeActivityDay
import com.ord.features.home.model.HomeSnapshot
import com.ord.features.home.model.HomeWindow
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class HomeSummaryRepository(
    private val template: R2dbcEntityTemplate,
) {
    private val objectMapper = ObjectMapper()
    fun load(
        userId: UUID,
        language: LanguageName,
        window: HomeWindow,
    ): Mono<HomeSnapshot> {
        return template.databaseClient
            .sql(SUMMARY_SQL)
            .bind("userId", userId)
            .bind("language", language.name)
            .bind("from30", window.from30Inclusive)
            .bind("fromMonth", window.fromMonthInclusive)
            .bind("toExclusive", window.toExclusive)
            .bind("yearStart", window.yearStartInclusive)
            .bind("yearEnd", window.yearEndExclusive)
            .map { row ->
                HomeSnapshot(
                    wordsTotal = cellLong(row, "words_total"),
                    wordsAddedLast30Days = cellLong(row, "words_added_last_30"),
                    wordsByType = parseTypeCounts(cellText(row, "words_by_type")),
                    conversationsTotal = cellLong(row, "conversations_total"),
                    conversationsCreatedLast30Days = cellLong(row, "conversations_created_last_30"),
                    messagesTotal = cellLong(row, "messages_total"),
                    messagesLast30Days = cellLong(row, "messages_last_30"),
                    gamesTotal = cellLong(row, "games_total"),
                    gamesLast30Days = cellLong(row, "games_last_30"),
                    activityDays = parseActivityDays(cellText(row, "activity_days")),
                )
            }
            .one()
    }

    private fun parseTypeCounts(json: String): Map<WordType, Long> {
        if (json.isBlank()) {
            return emptyMap()
        }

        val counts = linkedMapOf<WordType, Long>()
        for (field in objectMapper.readTree(json).properties()) {
            val type = runCatching { WordType.valueOf(field.key) }.getOrNull() ?: continue
            counts[type] = field.value.asLong()
        }
        return counts
    }

    private fun parseActivityDays(json: String): List<HomeActivityDay> {
        if (json.isBlank()) {
            return emptyList()
        }

        return objectMapper.readTree(json).map { day ->
            HomeActivityDay(
                date = day.path("date").asText(),
                count = day.path("count").asLong(),
            )
        }
    }

    private fun cellLong(row: io.r2dbc.spi.Readable, column: String): Long {
        val value = row.get(column) ?: return 0L
        return when (value) {
            is Long -> value
            is Int -> value.toLong()
            is Number -> value.toLong()
            else -> error("Unexpected numeric column $column: ${value::class.qualifiedName}")
        }
    }

    private fun cellText(row: io.r2dbc.spi.Readable, column: String): String {
        val value = row.get(column) ?: return ""
        return when (value) {
            is String -> value
            else -> value.toString()
        }
    }

    private companion object {
        val SUMMARY_SQL = """
            SELECT
                (
                    SELECT COUNT(*)
                    FROM words
                    WHERE user_id = :userId
                      AND language = :language
                ) AS words_total,
                (
                    SELECT COUNT(*)
                    FROM words
                    WHERE user_id = :userId
                      AND language = :language
                      AND created_at >= :fromMonth
                      AND created_at < :toExclusive
                ) AS words_added_last_30,
                (
                    SELECT COALESCE(jsonb_object_agg(grouped.type_name, grouped.cnt), '{}'::jsonb)::text
                    FROM (
                        SELECT type::text AS type_name, COUNT(*) AS cnt
                        FROM words
                        WHERE user_id = :userId
                          AND language = :language
                        GROUP BY type
                        HAVING COUNT(*) > 0
                    ) grouped
                ) AS words_by_type,
                (
                    SELECT COUNT(*)
                    FROM conversations
                    WHERE user_id = :userId
                      AND language = :language
                ) AS conversations_total,
                (
                    SELECT COUNT(*)
                    FROM conversations
                    WHERE user_id = :userId
                      AND language = :language
                      AND created_at >= :fromMonth
                      AND created_at < :toExclusive
                ) AS conversations_created_last_30,
                (
                    SELECT COUNT(*)
                    FROM conversation_messages cm
                    JOIN conversations c ON c.id = cm.conversation_id
                    WHERE c.user_id = :userId
                      AND c.language = :language
                ) AS messages_total,
                (
                    SELECT COUNT(*)
                    FROM conversation_messages cm
                    JOIN conversations c ON c.id = cm.conversation_id
                    WHERE c.user_id = :userId
                      AND c.language = :language
                      AND cm.created_at >= :fromMonth
                      AND cm.created_at < :toExclusive
                ) AS messages_last_30,
                (
                    SELECT COUNT(*)
                    FROM finished_games
                    WHERE user_id = :userId
                      AND language = :language
                ) AS games_total,
                (
                    SELECT COUNT(*)
                    FROM finished_games
                    WHERE user_id = :userId
                      AND language = :language
                      AND created_at >= :fromMonth
                      AND created_at < :toExclusive
                ) AS games_last_30,
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
                            WHERE user_id = :userId
                              AND language = :language
                              AND created_at >= :yearStart
                              AND created_at < :yearEnd
                            GROUP BY 1
                            UNION ALL
                            SELECT (cm.created_at AT TIME ZONE 'UTC')::date, COUNT(*)
                            FROM conversation_messages cm
                            JOIN conversations c ON c.id = cm.conversation_id
                            WHERE c.user_id = :userId
                              AND c.language = :language
                              AND cm.created_at >= :yearStart
                              AND cm.created_at < :yearEnd
                            GROUP BY 1
                            UNION ALL
                            SELECT (created_at AT TIME ZONE 'UTC')::date, COUNT(*)
                            FROM finished_games
                            WHERE user_id = :userId
                              AND language = :language
                              AND created_at >= :yearStart
                              AND created_at < :yearEnd
                            GROUP BY 1
                        ) events
                        GROUP BY activity_date
                        HAVING SUM(cnt) > 0
                    ) daily
                ) AS activity_days
        """.trimIndent()
    }
}
