package com.ord.features.home.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.conversation.models.conversation.enums.ConversationTone
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.home.model.HomeSnapshot
import com.ord.features.home.model.HomeWindow
import com.ord.features.home.model.parts.HomeActivityDay
import com.ord.features.home.model.parts.HomeRecentConversation
import com.ord.features.home.model.parts.HomeRecentWord
import io.r2dbc.spi.Readable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class HomeRepository(
    private val template: R2dbcEntityTemplate,
) {
    private val objectMapper = ObjectMapper()

    fun loadOverviews(
        userId: UUID,
        language: LanguageName,
        window: HomeWindow,
    ): Mono<HomeSnapshot> {
        return template.databaseClient
            .sql(OVERVIEWS_SQL)
            .bind("userId", userId)
            .bind("language", language.name)
            .bind("fromMonth", window.fromMonthInclusive)
            .bind("from90", window.from90Inclusive)
            .bind("toExclusive", window.toExclusive)
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
                    wordsAddedTrend90 = parseActivityDays(cellText(row, "words_added_trend_90")),
                    conversationsCreatedTrend90 = parseActivityDays(cellText(row, "conversations_created_trend_90")),
                    messagesTrend90 = parseActivityDays(cellText(row, "messages_trend_90")),
                    gamesFinishedTrend90 = parseActivityDays(cellText(row, "games_finished_trend_90")),
                )
            }
            .one()
    }

    fun loadActivityDays(
        userId: UUID,
        language: LanguageName,
        window: HomeWindow,
    ): Flux<HomeActivityDay> {
        return template.databaseClient
            .sql(ACTIVITY_DAYS_SQL)
            .bind("userId", userId)
            .bind("language", language.name)
            .bind("yearStart", window.yearStartInclusive)
            .bind("yearEnd", window.yearEndExclusive)
            .map { row ->
                HomeActivityDay(
                    date = row.get("activity_date", String::class.java)!!,
                    count = cellLong(row, "cnt"),
                )
            }
            .all()
    }

    fun loadRecentWords(
        userId: UUID,
        language: LanguageName,
        limit: Int,
    ): Flux<HomeRecentWord> {
        return template.databaseClient
            .sql(RECENT_WORDS_SQL)
            .bind("userId", userId)
            .bind("language", language.name)
            .bind("limit", limit)
            .map { row ->
                HomeRecentWord(
                    id = row.get("id", UUID::class.java)!!,
                    sourceWord = row.get("source_word", String::class.java)!!,
                    translation = row.get("translation", String::class.java)!!,
                    definitionPreview = row.get("definition_preview", String::class.java),
                    isBookmarked = row.get("is_bookmarked", Boolean::class.java)!!,
                    type = WordType.valueOf(row.get("type", String::class.java)!!),
                    extraMark = row.get("extra_mark", String::class.java)?.let { WordExtraMark.valueOf(it) },
                )
            }
            .all()
    }

    fun loadRecentConversations(
        userId: UUID,
        language: LanguageName,
        limit: Int,
    ): Flux<HomeRecentConversation> {
        return template.databaseClient
            .sql(RECENT_CONVERSATIONS_SQL)
            .bind("userId", userId)
            .bind("language", language.name)
            .bind("limit", limit)
            .map { row ->
                HomeRecentConversation(
                    id = row.get("id", UUID::class.java)!!,
                    topic = row.get("topic", String::class.java)!!,
                    type = ConversationType.valueOf(row.get("type", String::class.java)!!),
                    aiTone = ConversationTone.valueOf(row.get("ai_tone", String::class.java)!!),
                    aiInterlocutorName = row.get("ai_interlocutor_name", String::class.java)!!,
                    aiInterlocutorAvatarId = row.get("ai_interlocutor_avatar_id", String::class.java)!!,
                    updatedAt = row.get("updated_at", OffsetDateTime::class.java)!!.toInstant(),
                )
            }
            .all()
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

    private fun cellLong(row: Readable, column: String): Long {
        val value = row.get(column) ?: return 0L
        return when (value) {
            is Long -> value
            is Int -> value.toLong()
            is Number -> value.toLong()
            else -> error("Unexpected numeric column $column: ${value::class.qualifiedName}")
        }
    }

    private fun cellText(row: Readable, column: String): String {
        val value = row.get(column) ?: return ""
        return when (value) {
            is String -> value
            else -> value.toString()
        }
    }

    private companion object {
        const val DEFINITION_PREVIEW_LENGTH: Int = 160

        val OVERVIEWS_SQL = """
            -- -------------
            -- 1. words
            -- Lifetime total, the current UTC month, and counts by type.
            -- -------------
            SELECT (
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
                            ) grouped
                   ) AS words_by_type,
                   -- -------------
                   -- 2. conversations
                   -- Lifetime total and the current UTC month. created_at, not message activity.
                   -- -------------
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
                   -- -------------
                   -- 3. messages
                   -- Counted through this user's conversations.
                   -- -------------
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
                   -- -------------
                   -- 4. finished games
                   -- Lifetime total and the current UTC month.
                   -- -------------
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
                   -- -------------
                   -- 5. trends
                   -- Sparse UTC days over the last 90 days. Empty input becomes [].
                   -- -------------
                   -- words added
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
                                WHERE user_id = :userId
                                  AND language = :language
                                  AND created_at >= :from90
                                  AND created_at < :toExclusive
                                GROUP BY 1
                            ) daily
                   ) AS words_added_trend_90,
                   -- conversations created
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
                                WHERE user_id = :userId
                                  AND language = :language
                                  AND created_at >= :from90
                                  AND created_at < :toExclusive
                                GROUP BY 1
                            ) daily
                   ) AS conversations_created_trend_90,
                   -- messages
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
                                WHERE c.user_id = :userId
                                  AND c.language = :language
                                  AND cm.created_at >= :from90
                                  AND cm.created_at < :toExclusive
                                GROUP BY 1
                            ) daily
                   ) AS messages_trend_90,
                   -- games finished
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
                                WHERE user_id = :userId
                                  AND language = :language
                                  AND created_at >= :from90
                                  AND created_at < :toExclusive
                                GROUP BY 1
                            ) daily
                   ) AS games_finished_trend_90
        """.trimIndent()

        val ACTIVITY_DAYS_SQL = """
            -- -------------
            -- Year heatmap
            -- Words, messages, and finished games collapse into one count per UTC day.
            -- -------------
            SELECT to_char(daily.activity_date, 'YYYY-MM-DD') AS activity_date,
                   daily.cnt
            FROM (
                     SELECT activity_date, SUM(cnt)::bigint AS cnt
                     FROM (
                              -- words
                              SELECT (created_at AT TIME ZONE 'UTC')::date AS activity_date, COUNT(*) AS cnt
                              FROM words
                              WHERE user_id = :userId
                                AND language = :language
                                AND created_at >= :yearStart
                                AND created_at < :yearEnd
                              GROUP BY 1
                              UNION ALL
                              -- messages
                              SELECT (cm.created_at AT TIME ZONE 'UTC')::date, COUNT(*)
                              FROM conversation_messages cm
                                       JOIN conversations c ON c.id = cm.conversation_id
                              WHERE c.user_id = :userId
                                AND c.language = :language
                                AND cm.created_at >= :yearStart
                                AND cm.created_at < :yearEnd
                              GROUP BY 1
                              UNION ALL
                              -- finished games
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
            ORDER BY daily.activity_date
        """.trimIndent()

        val RECENT_WORDS_SQL = """
            -- -------------
            -- Recent words
            -- Definition is truncated. No progress and no banks.
            -- -------------
            SELECT
                id,
                source_word,
                translation,
                left(definition, $DEFINITION_PREVIEW_LENGTH) AS definition_preview,
                is_bookmarked,
                type,
                extra_mark
            FROM words
            WHERE user_id = :userId
              AND language = :language
            ORDER BY created_at DESC, id DESC
            LIMIT :limit
        """.trimIndent()

        val RECENT_CONVERSATIONS_SQL = """
            -- -------------
            -- Recent conversations
            -- Topic, tone, and interlocutor. updated_at only.
            -- -------------
            SELECT
                id,
                topic,
                type,
                ai_tone,
                ai_interlocutor_name,
                ai_interlocutor_avatar_id,
                updated_at
            FROM conversations
            WHERE user_id = :userId
              AND language = :language
            ORDER BY updated_at DESC, id DESC
            LIMIT :limit
        """.trimIndent()
    }
}
