package com.ord.features.conversation.api.facades.impl

import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.ConversationCRUDFacade
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.mappers.ConversationMapper
import com.ord.features.conversation.services.ConversationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConversationCRUDFacadeImpl(
    private val conversationService: ConversationService,
    private val conversationMapper: ConversationMapper,
    private val languageProficiencyService: LanguageProficiencyService,
) : ConversationCRUDFacade {

    internal fun ConversationEntity.toDTO(): ConversationDTO {
        return conversationMapper.toDTO(this)
    }

    override fun createConversation(
        user: UserEntity,
        body: CreateConversationRequest
    ): ResponseEntity<ConversationDTO> {
        val proficiency = languageProficiencyService.findUserProficiencyInLanguageOrThrow(user.id, body.language)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                conversationService.save(
                    ConversationEntity(
                        topic = body.topic,
                        additionalContext = body.additionalContext ?: "",
                        language = body.language,
                        proficiencyLevel = proficiency.proficiency,
                        goal = body.goal,
                        aiTone = body.tone,
                        aiResponseLength = body.aiResponseLength,
                        user = user
                    )
                ).toDTO()
            )
    }

    override fun getManyConversations(user: UserEntity): ResponseEntity<List<ConversationDTO>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                conversationService
                    .findAll(user.id)
                    .map { it.toDTO() }
            )
    }

    override fun getConversationById(
        user: UserEntity,
        conversationId: UUID
    ): ResponseEntity<ConversationDTO> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                conversationService.findByIdOrFail(
                    id = conversationId,
                    userId = user.id
                ).toDTO()
            )
    }

    override fun deleteConversation(
        user: UserEntity,
        conversationId: UUID
    ): ResponseEntity<Unit> {
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(
                conversationService.deleteById(
                    id = conversationId,
                    userId = user.id,
                )
            )
    }
}