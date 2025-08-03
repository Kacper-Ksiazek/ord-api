package com.ord.features.conversation.models.entities

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.models.enums.ConversationAIResponseLength
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.features.conversation.models.enums.ConversationTone
import com.ord.shared.models.IdentifiableUserResource
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "conversations")
data class ConversationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @Column(name = "topic", nullable = false)
    var topic: String,

    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    var language: LanguageName,

    @Column(name = "proficiency_level", nullable = false)
    @Enumerated(EnumType.STRING)
    var proficiencyLevel: LanguageProficiencyLevel,

    @Column(name = "goal", nullable = false)
    @Enumerated(EnumType.STRING)
    var goal: ConversationGoal,

    @Column(name = "ai_tone", nullable = false)
    @Enumerated(EnumType.STRING)
    var aiTone: ConversationTone,

    @Column(name = "ai_response_length", nullable = false)
    @Enumerated(EnumType.STRING)
    var aiResponseLength: ConversationAIResponseLength,

    @Column(name = "additional_context", nullable = false, columnDefinition = "TEXT")
    var additionalContext: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    override var user: UserEntity,

    @Column(name = "user_id", insertable = false, updatable = false)
    var userId: UUID = user.id,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource {
    @PostLoad
    fun populateUserId() {
        userId = user.id
    }
}
