package com.ord.features.conversation.models.entities

import com.ord.features.conversation.models.enums.ConversationMessageSender
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "conversation_messages")
data class ConversationMessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "message_order", nullable = false)
    var messageOrder: Int,

    @Column(name = "sender", nullable = false)
    @Enumerated(EnumType.STRING)
    var sender: ConversationMessageSender,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(name = "conversation_id", nullable = false)
    var conversationId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false, insertable = false, updatable = false)
    @Transient
    var conversation: ConversationEntity? = null,

    @OneToOne(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        optional = true
    )
    @JoinColumn(name = "feedback_id", referencedColumnName = "id")
    var feedback: ConversationUserMessageFeedbackEntity? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
)
