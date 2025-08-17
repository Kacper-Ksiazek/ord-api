package com.ord.features.conversation.models.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "conversation_user_message_feedback")
data class ConversationUserMessageFeedbackEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "grammar", nullable = false)
    var grammar: Int,

    @Column(name = "vocabulary", nullable = false)
    var vocabulary: Int,

    @Column(name = "answer_length", nullable = false)
    var answerLength: Int,

    @Column(name = "suggested_answer", columnDefinition = "TEXT")
    var suggestedAnswer: String? = null,

    @Column(name = "comment", columnDefinition = "TEXT")
    var comment: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
)
