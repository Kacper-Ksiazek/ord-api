package com.ord.features.conversation.models.mappers

import com.ord.core.user.model.UserMapper
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class ConversationMapper(
    private val userMapper: UserMapper,
    private val conversationMessageMapper: ConversationMessageMapper,
) : MapperBase<ConversationEntity, ConversationDTO> {

    override fun toEntity(dto: ConversationDTO): ConversationEntity {
        return ConversationEntity(
            id = dto.id,
            topic = dto.topic,
            language = dto.language,
            proficiencyLevel = dto.proficiencyLevel,
            goal = dto.goal,
            aiTone = dto.aiTone,
            aiResponseLength = dto.aiResponseLength,
            additionalContext = dto.additionalContext,

            user = userMapper.toEntity(dto.user),
            userId = dto.userId,
            messages = dto.messages.map { conversationMessageMapper.toEntity(it) }.toMutableList(),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: ConversationEntity): ConversationDTO {
        return ConversationDTO(
            id = entity.id,
            topic = entity.topic,
            language = entity.language,
            proficiencyLevel = entity.proficiencyLevel,
            goal = entity.goal,
            aiTone = entity.aiTone,
            aiResponseLength = entity.aiResponseLength,
            additionalContext = entity.additionalContext,

            user = userMapper.toDTO(entity.user),
            userId = entity.userId,
            messages = entity.messages.map { conversationMessageMapper.toDTO(it) }.toMutableList(),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
