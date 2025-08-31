package com.ord.features.conversation.models.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("conversation_user_message_feedback")
data class ConversationUserMessageFeedbackEntity(
    @Id
    var id: UUID? = null, // DB generates uuid_generate_v4()

    @Column("grammar")
    var grammar: Int,

    @Column("vocabulary")
    var vocabulary: Int,

    @Column("answer_length")
    var answerLength: Int,

    @Column("suggested_answer")
    var suggestedAnswer: String? = null,

    @Column("comment")
    var comment: String? = null,

    @Column("created_at")
    var createdAt: Instant = Instant.now(),
)

