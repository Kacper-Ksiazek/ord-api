package com.ord.features.conversation.repositories.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.conversation.models.conversation.ConversationDTO
import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsDTO
import com.ord.features.conversation.models.conversation_ai_message_learning_tips.ConversationAIMessageLearningTipsMapper
import com.ord.features.conversation.models.conversation_message.ConversationMessageDTO
import com.ord.features.conversation.models.conversation_user_message_feedback.ConversationUserMessageFeedbackDTO
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.models.conversation.enums.ConversationTone
import com.ord.features.conversation.models.conversation_user_message_feedback.ConversationUserMessageFeedbackMapper
import com.ord.features.conversation.models.dto.RecentConversationInfo
import com.ord.features.conversation.repositories.ConversationRepositoryCustomMethods
import io.r2dbc.postgresql.codec.Json
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.*

@Repository
class ConversationRepositoryCustomMethodsImpl(
    private val template: R2dbcEntityTemplate,
    private val feedbackMapper: ConversationUserMessageFeedbackMapper,
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
                -- User message: Feedback
                f.id as feedback_id,
                f.tutor_comment as feedback_tutor_comment,
                f.grammar as feedback_grammar,
                f.vocabulary as feedback_vocabulary,
                f.answer_length as feedback_answer_length,
                f.naturalness as feedback_naturalness,
                f.coherence_with_context as feedback_coherence_with_context,
                f.register_appropriate as feedback_register_appropriate,
                f.mistakes as feedback_mistakes,
                f.strengths_identified as feedback_strengths_identified,
                f.vocabulary_enrichment as feedback_vocabulary_enrichment,
                f.alternative_expressions as feedback_alternative_expressions,
                f.cultural_note as feedback_cultural_note,
                -- AI message: Learning tips
                lt.id as learning_tips_id,
                lt.grammar_tips as learning_tips_grammar_tips,
                lt.vocabulary_tips as learning_tips_vocabulary_tips,
                lt.idiom_tips as learning_tips_idiom_tips,
                lt.message_id as learning_tips_message_id
            FROM conversations c
            LEFT JOIN conversation_messages m ON c.id = m.conversation_id
            LEFT JOIN conversation_user_message_feedback f ON m.id = f.message_id
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
                            val feedback = if (row["feedback_id"] != null) {
                                ConversationUserMessageFeedbackDTO(
                                    id = row["feedback_id"] as UUID,
                                    tutorComment = row["feedback_tutor_comment"] as String,
                                    grammar = row["feedback_grammar"] as Int,
                                    vocabulary = row["feedback_vocabulary"] as Int,
                                    answerLength = row["feedback_answer_length"] as Int,
                                    naturalness = row["feedback_naturalness"] as Int,
                                    coherenceWithContext = row["feedback_coherence_with_context"] as Int,
                                    registerAppropriate = row["feedback_register_appropriate"] as Boolean,
                                    mistakes = feedbackMapper.deserializeMistakes(row["feedback_mistakes"] as Json),
                                    strengthsIdentified = feedbackMapper.deserializeStringSet(row["feedback_strengths_identified"] as Json),
                                    vocabularyEnrichment = feedbackMapper.deserializeVocabularyEnrichment(row["feedback_vocabulary_enrichment"] as Json),
                                    alternativeExpressions = feedbackMapper.deserializeAlternativeExpressions(row["feedback_alternative_expressions"] as Json),
                                    culturalNote = row["feedback_cultural_note"] as String?,
                                    messageId = row["message_id"] as UUID
                                )
                            } else null

                            val learningTips = if (row["learning_tips_id"] != null) {
                                ConversationAIMessageLearningTipsDTO(
                                    id = row["learning_tips_id"] as UUID,
                                    grammarTips = learningTipsMapper.deserializeGrammarTips(row["learning_tips_grammar_tips"] as Json),
                                    vocabularyTips = learningTipsMapper.deserializeVocabularyTips(row["learning_tips_vocabulary_tips"] as Json),
                                    idiomTips = learningTipsMapper.deserializeIdiomTips(row["learning_tips_idiom_tips"] as Json),
                                    messageId = row["learning_tips_message_id"] as UUID
                                )
                            } else null

                            ConversationMessageDTO(
                                id = row["message_id"] as UUID,
                                messageOrder = row["message_order"] as Int,
                                sender = ConversationMessageSender.valueOf(row["sender"] as String),
                                content = row["content"] as String,
                                feedback = feedback,
                                learningTips = learningTips,
                                createdAt = (row["message_created_at"] as OffsetDateTime).toInstant()
                            )
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
}