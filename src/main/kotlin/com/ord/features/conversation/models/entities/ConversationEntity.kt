package com.ord.features.conversation.models.entities

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.models.enums.ConversationAIResponseLength
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.features.conversation.models.enums.ConversationTone
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("conversations")
data class ConversationEntity(
    @Id
    override var id: UUID? = null,

    @Column("topic")
    var topic: String,

    @Column("language")
    var language: LanguageName,

    @Column("proficiency_level")
    var proficiencyLevel: LanguageProficiencyLevel,

    @Column("goal")
    var goal: ConversationGoal,

    @Column("ai_tone")
    var aiTone: ConversationTone,

    @Column("ai_response_length")
    var aiResponseLength: ConversationAIResponseLength,

    @Column("additional_context")
    var additionalContext: String? = null,

    @Column("user_id")
    override var userId: UUID,

    @Column("created_at")
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    var updatedAt: Instant = Instant.now(),

    @Transient
    override var user: UserEntity? = null,

    @Transient
    var messages: MutableList<ConversationMessageEntity> = mutableListOf()
) : IdentifiableUserResource
