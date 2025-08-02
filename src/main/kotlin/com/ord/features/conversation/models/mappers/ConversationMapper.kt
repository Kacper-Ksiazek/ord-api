package com.ord.features.conversation.models.mappers

import com.ord.core.user.model.UserMapper
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class ConversationMapper(
    private val userMapper: UserMapper
) : MapperBase<ConversationEntity, ConversationDTO> {

    override fun toEntity(dto: ConversationDTO): ConversationEntity {
        return ConversationEntity(
            id = dto.id,
            language = dto.language,
            proficiencyLevel = dto.proficiencyLevel,
            goal = dto.goal,
            aiTone = dto.aiTone,
            aiResponseLength = dto.aiResponseLength,

            user = userMapper.toEntity(dto.user),
            userId = dto.userId,

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: ConversationEntity): ConversationDTO {
        return ConversationDTO(
            id = entity.id,
            language = entity.language,
            proficiencyLevel = entity.proficiencyLevel,
            goal = entity.goal,
            aiTone = entity.aiTone,
            aiResponseLength = entity.aiResponseLength,

            user = userMapper.toDTO(entity.user),
            userId = entity.userId,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}

