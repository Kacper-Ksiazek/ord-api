package com.ord.features.conversation.repositories.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.conversation.models.conversation.ConversationDTO
import com.ord.features.conversation.models.conversation.ConversationEntity
import com.ord.features.conversation.models.conversation.ConversationListFilters
import com.ord.features.conversation.models.conversation.recencyBucketToInstantRange
import com.ord.features.conversation.models.conversation_activity.DailyActivityCount
import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsDTO
import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsMapper
import com.ord.features.conversation.models.conversation_message.ConversationAIMessageDTO
import com.ord.features.conversation.models.conversation_message.ConversationMessageDTO
import com.ord.features.conversation.models.conversation_message.ConversationUserMessageDTO
import com.ord.features.conversation.models.conversation_user_message_analysis.ConversationUserMessageAnalysisDTO
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.conversation.enums.ConversationTone
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.models.conversation_user_message_analysis.ConversationUserMessageAnalysisMapper
import com.ord.features.conversation.models.dto.RecentConversationInfo
import com.ord.features.conversation.repositories.ConversationRepositoryCustomMethods
import io.r2dbc.postgresql.codec.Json
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

@Repository
class ConversationRepositoryCustomMethodsImpl(
    private val template: R2dbcEntityTemplate,
    private val analysisMapper: ConversationUserMessageAnalysisMapper,
    private val learningTipsMapper: ConversationAIMessageLearningTipsMapper
) : ConversationRepositoryCustomMethods {
    override fun findRecentTopics(
        userId: UUID,
        type: ConversationType,
        language: LanguageName,
        limit: Int
    ): Flux<String> {
        val query = """
            SELECT c.topic
            FROM conversations c
            WHERE c.user_id = :userId
                AND c.type= :type
                AND c.language = :language
            ORDER BY c.created_at DESC
            LIMIT :limit
        """

        return template.databaseClient
            .sql(query)
            .bind("userId", userId)
            .bind("type", type.name)
            .bind("language", language.name)
            .bind("limit", limit)
            .map { row -> row.get("topic", String::class.java)!! }
            .all()
    }

    override fun findRecentConversationsInfo(
        userId: UUID,
        type: ConversationType,
        language: LanguageName,
        limit: Int
    ): Flux<RecentConversationInfo> {
        val query = """
            SELECT c.ai_interlocutor_avatar_id, c.ai_interlocutor_name, c.topic
            FROM conversations c
            WHERE c.user_id = :userId
                AND c.type = :type
                AND c.language = :language
            ORDER BY c.created_at DESC
            LIMIT :limit
        """

        return template.databaseClient
            .sql(query)
            .bind("userId", userId)
            .bind("type", type.name)
            .bind("language", language.name)
            .bind("limit", limit)
            .map { row ->
                RecentConversationInfo(
                    avatarId = row.get("ai_interlocutor_avatar_id", String::class.java)!!,
                    name = row.get("ai_interlocutor_name", String::class.java)!!,
                    topic = row.get("topic", String::class.java)!!
                )
            }
            .all()
    }


    override fun findByIdOrFailWithMessages(
        id: UUID,
        userId: UUID
    ): Mono<ConversationDTO> {
        val query = """
            SELECT
                c.*,
                m.id as message_id,
                m.conversation_id,
                m.sender,
                m.content,
                m.message_order,
                m.created_at as message_created_at,
                -- User message: Analysis
                f.id as analysis_id,
                f.tutor_comment as analysis_tutor_comment,
                f.corrected_message as analysis_corrected_message,
                f.grammar as analysis_grammar,
                f.vocabulary as analysis_vocabulary,
                f.naturalness as analysis_naturalness,
                f.coherence_with_context as analysis_coherence_with_context,
                f.mistakes as analysis_mistakes,
                f.strengths as analysis_strengths,
                f.suggestions as analysis_suggestions,
                -- AI message: Learning tips
                lt.id as learning_tips_id,
                lt.grammar_tips as learning_tips_grammar_tips,
                lt.vocabulary_tips as learning_tips_vocabulary_tips,
                lt.phrase_tips as learning_tips_phrase_tips,
                lt.message_id as learning_tips_message_id
            FROM conversations c
            LEFT JOIN conversation_messages m ON c.id = m.conversation_id
            LEFT JOIN conversation_user_message_analysis f ON m.id = f.message_id
            LEFT JOIN conversation_ai_message_learning_tips lt ON m.id = lt.message_id
            WHERE c.user_id = :userId
                AND c.id = :id
            ORDER BY m.message_order
        """

        return template.databaseClient
            .sql(query)
            .bind("id", id)
            .bind("userId", userId)
            .fetch()
            .all()
            .collectList()
            .flatMap { rows ->
                if (rows.isEmpty()) {
                    Mono.error(NotFoundException("Conversation not found"))
                } else {
                    val firstRow = rows[0]

                    val messages = rows
                        .filter { it["message_id"] != null }
                        .distinctBy { it["message_id"] }
                        .map { row ->
                            val analysis = if (row["analysis_id"] != null) {
                                ConversationUserMessageAnalysisDTO(
                                    id = row["analysis_id"] as UUID,
                                    tutorComment = row["analysis_tutor_comment"] as String,
                                    grammar = row["analysis_grammar"] as Int,
                                    vocabulary = row["analysis_vocabulary"] as Int,
                                    naturalness = row["analysis_naturalness"] as Int,
                                    coherenceWithContext = row["analysis_coherence_with_context"] as Int,
                                    mistakes = analysisMapper.deserializeMistakes(row["analysis_mistakes"] as Json),
                                    strengths = analysisMapper.deserializeStrengths(row["analysis_strengths"] as Json),
                                    suggestions = analysisMapper.deserializeSuggestions(row["analysis_suggestions"] as Json),
                                    messageId = row["message_id"] as UUID,
                                    correctedMessage = row["analysis_corrected_message"] as String?
                                )
                            } else null

                            val learningTips = if (row["learning_tips_id"] != null) {
                                ConversationAIMessageLearningTipsDTO(
                                    id = row["learning_tips_id"] as UUID,
                                    grammarTips = learningTipsMapper.deserializeGrammarTips(row["learning_tips_grammar_tips"] as Json),
                                    vocabularyTips = learningTipsMapper.deserializeVocabularyTips(row["learning_tips_vocabulary_tips"] as Json),
                                    phraseTips = learningTipsMapper.deserializePhraseTips(row["learning_tips_phrase_tips"] as Json),
                                    messageId = row["learning_tips_message_id"] as UUID
                                )
                            } else null

                            val sender = ConversationMessageSender.valueOf(row["sender"] as String)
                            when (sender) {
                                ConversationMessageSender.USER -> ConversationUserMessageDTO(
                                    id = row["message_id"] as UUID,
                                    messageOrder = row["message_order"] as Int,
                                    content = row["content"] as String,
                                    analysis = analysis,
                                    createdAt = (row["message_created_at"] as OffsetDateTime).toInstant()
                                )
                                ConversationMessageSender.AI -> ConversationAIMessageDTO(
                                    id = row["message_id"] as UUID,
                                    messageOrder = row["message_order"] as Int,
                                    content = row["content"] as String,
                                    learningTips = learningTips,
                                    createdAt = (row["message_created_at"] as OffsetDateTime).toInstant()
                                )
                            }
                        }.toMutableList()

                    Mono.just(
                        ConversationDTO(
                            id = firstRow["id"] as UUID,
                            topic = firstRow["topic"] as String,
                            language = LanguageName.valueOf(firstRow["language"] as String),
                            proficiencyLevel = LanguageProficiencyLevel.valueOf(firstRow["proficiency_level"] as String),
                            type = ConversationType.valueOf(firstRow["type"] as String),
                            aiTone = ConversationTone.valueOf(firstRow["ai_tone"] as String),
                            additionalContext = firstRow["additional_context"] as String?,
                            aiInterlocutorName = firstRow["ai_interlocutor_name"] as String,
                            aiInterlocutorAvatarId = firstRow["ai_interlocutor_avatar_id"] as String,
                            messages = messages,
                            createdAt = (firstRow["created_at"] as OffsetDateTime).toInstant(),
                            updatedAt = (firstRow["updated_at"] as OffsetDateTime).toInstant()
                        )
                    )
                }
            }
    }

    override fun findAllWithFilters(userId: UUID, filters: ConversationListFilters): Flux<ConversationEntity> {
        val now = Instant.now()

        val conditions = buildList {
            add("c.user_id = :userId")
            if (filters.search != null) add("(LOWER(c.topic) LIKE :search OR LOWER(c.ai_interlocutor_name) LIKE :search)")
            if (filters.type != null) add("c.type = :type")
            if (filters.recencyBucket != null) {
                val range = recencyBucketToInstantRange(filters.recencyBucket, now)
                if (range.from != null) add("c.updated_at >= :bucketFrom")
                if (range.until != null) add("c.updated_at < :bucketUntil")
            }
        }.joinToString(" AND ")

        val bindings = mutableMapOf<String, Any>("userId" to userId).apply {
            filters.search?.let { put("search", "%${it.lowercase()}%") }
            filters.type?.let { put("type", it.name) }
            filters.recencyBucket?.let {
                val range = recencyBucketToInstantRange(it, now)
                range.from?.let { f -> put("bucketFrom", f) }
                range.until?.let { u -> put("bucketUntil", u) }
            }
        }

        val query = "SELECT * FROM conversations c WHERE $conditions ORDER BY c.updated_at DESC, c.id DESC"

        return template.databaseClient
            .sql(query)
            .bindValues(bindings)
            .map { row ->
                ConversationEntity(
                    id = row["id"] as UUID,
                    topic = row["topic"] as String,
                    additionalContext = row["additional_context"] as String?,
                    language = LanguageName.valueOf(row["language"] as String),
                    proficiencyLevel = LanguageProficiencyLevel.valueOf(row["proficiency_level"] as String),
                    type = ConversationType.valueOf(row["type"] as String),
                    aiTone = ConversationTone.valueOf(row["ai_tone"] as String),
                    aiInterlocutorName = row["ai_interlocutor_name"] as String,
                    aiInterlocutorAvatarId = row["ai_interlocutor_avatar_id"] as String,
                    userId = row["user_id"] as UUID,
                    createdAt = (row["created_at"] as OffsetDateTime).toInstant(),
                    updatedAt = (row["updated_at"] as OffsetDateTime).toInstant()
                )
            }
            .all()
    }

    override fun countDailyNewConversations(userId: UUID, from: Instant, to: Instant): Flux<DailyActivityCount> {
        val query = """
            SELECT DATE(c.created_at AT TIME ZONE 'UTC') AS activity_date, COUNT(*) AS cnt
            FROM conversations c
            WHERE c.user_id = :userId
              AND c.created_at >= :from
              AND c.created_at < :to
            GROUP BY activity_date
            ORDER BY activity_date
        """

        return template.databaseClient
            .sql(query)
            .bind("userId", userId)
            .bind("from", from)
            .bind("to", to)
            .map { row ->
                DailyActivityCount(
                    date = row.get("activity_date", LocalDate::class.java)!!,
                    count = row.get("cnt", java.lang.Long::class.java)!!.toLong(),
                )
            }
            .all()
    }
}