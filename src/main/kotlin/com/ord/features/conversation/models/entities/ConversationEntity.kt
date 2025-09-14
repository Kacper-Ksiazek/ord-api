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
    override val id: UUID = UUID.randomUUID(),

    val topic: String,
    val additionalContext: String? = null,

    val language: LanguageName,
    val proficiencyLevel: LanguageProficiencyLevel,

    val goal: ConversationGoal,
    val aiTone: ConversationTone,
    val aiResponseLength: ConversationAIResponseLength,

    override var userId: UUID,

    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),

    @Transient override var user: UserEntity? = null,
    @Transient var messages: MutableList<ConversationMessageEntity> = mutableListOf()
) : IdentifiableUserResource
