package com.ord.features.conversation.models.entities

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.models.enums.ConversationType
import com.ord.features.conversation.models.enums.ConversationTone
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

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
