package com.ord.features.conversation.repositories.impl

import com.ord.features.conversation.models.conversation_activity.DailyActivityCount
import com.ord.features.conversation.models.conversation_message.ConversationMessageEntity
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.repositories.ConversationMessageRepositoryCustomMethods
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Repository
class ConversationMessageRepositoryCustomMethodsImpl(
    private val template: R2dbcEntityTemplate
) : ConversationMessageRepositoryCustomMethods {

    override fun findLatestAIMessageWithoutLearningTips(conversationId: UUID): Mono<ConversationMessageEntity> {
        val query = """
            SELECT m.*
            FROM conversation_messages m
            LEFT JOIN conversation_ai_message_learning_tips lt ON m.id = lt.message_id
            WHERE m.conversation_id = :conversationId
              AND m.sender = :sender
              AND lt.id IS NULL
            ORDER BY m.message_order DESC
            LIMIT 1
        """

        return template.databaseClient
            .sql(query)
            .bind("conversationId", conversationId)
            .bind("sender", ConversationMessageSender.AI.name)
            .map { row ->
                ConversationMessageEntity(
                    id = row.get("id", UUID::class.java)!!,
                    content = row.get("content", String::class.java)!!,
                    messageOrder = row.get("message_order", Int::class.java)!!,
                    sender = ConversationMessageSender.valueOf(row.get("sender", String::class.java)!!),
                    conversationId = row.get("conversation_id", UUID::class.java)!!,
                    createdAt = row.get("created_at", Instant::class.java)!!
                )
            }
            .one()
    }

    override fun countDailyMessages(userId: UUID, from: Instant, to: Instant): Flux<DailyActivityCount> {
        val query = """
            SELECT DATE(cm.created_at AT TIME ZONE 'UTC') AS activity_date, COUNT(*) AS cnt
            FROM conversation_messages cm
            JOIN conversations c ON cm.conversation_id = c.id
            WHERE c.user_id = :userId
              AND cm.sender = :sender
              AND cm.created_at >= :from
              AND cm.created_at < :to
            GROUP BY activity_date
            ORDER BY activity_date
        """

        return template.databaseClient
            .sql(query)
            .bind("userId", userId)
            .bind("sender", ConversationMessageSender.USER.name)
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
