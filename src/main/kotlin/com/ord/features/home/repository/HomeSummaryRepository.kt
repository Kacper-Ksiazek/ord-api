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
            .bind("fromMonth", window.fromMonthInclusive)
            .bind("from90", window.from90Inclusive)
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
                    wordsAddedTrend90 = parseActivityDays(cellText(row, "words_added_trend_90")),
                    conversationsCreatedTrend90 = parseActivityDays(cellText(row, "conversations_created_trend_90")),
                    messagesTrend90 = parseActivityDays(cellText(row, "messages_trend_90")),
                    gamesFinishedTrend90 = parseActivityDays(cellText(row, "games_finished_trend_90")),
                )
            }
            .one()
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

        val SUMMARY_SQL = """
            SELECT *
            FROM home_summary(
                :userId,
                :language,
                :fromMonth,
                :from90,
                :toExclusive,
                :yearStart,
                :yearEnd
            )
        """.trimIndent()

        val RECENT_WORDS_SQL = """
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
