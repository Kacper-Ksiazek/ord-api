package com.ord.features.conversation.models.conversation

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.models.conversation_message.ConversationMessageEntity
import com.ord.features.conversation.models.conversation.enums.ConversationTone
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("conversations")
data class ConversationEntity(
    @Id
    override val id: UUID? = null,

    val topic: String,
    val additionalContext: String? = null,

    val language: LanguageName,
    val proficiencyLevel: LanguageProficiencyLevel,

    val type: ConversationType,
    val aiTone: ConversationTone,
    val aiInterlocutorName: String? = null,
    val aiInterlocutorAvatarId: String? = null,

    override var userId: UUID,

    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource {
    @Transient
    var messages: MutableList<ConversationMessageEntity> = mutableListOf()
}