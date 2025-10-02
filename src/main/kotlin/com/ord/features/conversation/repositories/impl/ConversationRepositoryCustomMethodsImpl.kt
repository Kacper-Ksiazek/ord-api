package com.ord.features.conversation.repositories.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.features.conversation.models.dto.ConversationMessageDTO
import com.ord.features.conversation.models.dto.ConversationUserMessageFeedbackDTO
import com.ord.features.conversation.models.enums.ConversationType
import com.ord.features.conversation.models.enums.ConversationMessageSender
import com.ord.features.conversation.models.enums.ConversationTone
import com.ord.features.conversation.repositories.ConversationRepositoryCustomMethods
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.*

@Repository
class ConversationRepositoryCustomMethodsImpl(
    private val template: R2dbcEntityTemplate
) : ConversationRepositoryCustomMethods {
    override fun findRecentTopics(
        userId: UUID,
        goal: ConversationType,
        language: LanguageName,
        limit: Int
    ): Flux<String> {
        val query = """
            SELECT c.topic
            FROM conversations c
            WHERE c.user_id = :userId
                AND c.goal = :goal
                AND c.language = :language
            ORDER BY c.created_at DESC
            LIMIT :limit
        """

        return template.databaseClient
            .sql(query)
            .bind("userId", userId)
            .bind("goal", goal.name)
            .bind("language", language.name)
            .bind("limit", limit)
            .map { row -> row.get("topic", String::class.java)!! }
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
                f.id as feedback_id,
                f.grammar as feedback_grammar,
                f.vocabulary as feedback_vocabulary,
                f.answer_length as feedback_answer_length,
                f.suggested_answer as feedback_suggested_answer,
                f.comment as feedback_comment
            FROM conversations c
            LEFT JOIN conversation_messages m ON c.id = m.conversation_id
            LEFT JOIN conversation_user_message_feedback f ON m.id = f.message_id
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
                    Mono.error(RuntimeException("Conversation not found"))
                } else {
                    val firstRow = rows[0]

                    val messages = rows
                        .filter { it["message_id"] != null }
                        .distinctBy { it["message_id"] }
                        .map { row ->
                            val feedback = if (row["feedback_id"] != null) {
                                ConversationUserMessageFeedbackDTO(
                                    id = row["feedback_id"] as UUID,
                                    grammar = row["feedback_grammar"] as Int,
                                    vocabulary = row["feedback_vocabulary"] as Int,
                                    answerLength = row["feedback_answer_length"] as Int,
                                    comment = row["feedback_comment"] as String?,
                                    suggestedAnswer = row["feedback_suggested_answer"] as String?,
                                    messageId = row["message_id"] as UUID
                                )
                            } else null

                            ConversationMessageDTO(
                                id = row["message_id"] as UUID,
                                messageOrder = row["message_order"] as Int,
                                sender = ConversationMessageSender.valueOf(row["sender"] as String),
                                content = row["content"] as String,
                                feedback = feedback,
                                createdAt = (row["message_created_at"] as OffsetDateTime).toInstant()
                            )
                        }.toMutableList()

                    Mono.just(
                        ConversationDTO(
                            id = firstRow["id"] as UUID,
                            topic = firstRow["topic"] as String,
                            language = LanguageName.valueOf(firstRow["language"] as String),
                            proficiencyLevel = LanguageProficiencyLevel.valueOf(firstRow["proficiency_level"] as String),
                            type = ConversationType.valueOf(firstRow["goal"] as String),
                            aiTone = ConversationTone.valueOf(firstRow["ai_tone"] as String),
                            additionalContext = firstRow["additional_context"] as String?,
                            aiInterlocutorName =  firstRow["ai_interlocutor_name"] as String?,
                            aiInterlocutorAvatarId =   firstRow["ai_interlocutor_avatar"] as String?,
                            messages = messages,
                            createdAt = (firstRow["created_at"] as OffsetDateTime).toInstant(),
                            updatedAt = (firstRow["updated_at"] as OffsetDateTime).toInstant()
                        )
                    )
                }
            }
    }
}