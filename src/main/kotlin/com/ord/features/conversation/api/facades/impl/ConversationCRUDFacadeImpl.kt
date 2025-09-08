package com.ord.features.conversation.api.facades.impl

import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.security.UserRepositoryReactive
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
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class ConversationCRUDFacadeImpl(
    private val conversationService: ConversationService,
    private val conversationMapper: ConversationMapper,
    private val languageProficiencyService: LanguageProficiencyService,
    private val userRepositoryReactive: UserRepositoryReactive,
) : ConversationCRUDFacade {

    internal fun ConversationEntity.toDTO(): ConversationDTO {
        return conversationMapper.toDTO(this)
    }

    override fun createConversation(
        user: UserEntity,
        body: CreateConversationRequest
    ): Mono<ResponseEntity<ConversationDTO>> {
        return languageProficiencyService.findUserProficiencyInLanguageOrThrow(user.id, body.language)
            .flatMap { proficiency ->
                conversationService.save(
                    ConversationEntity(
                        topic = body.topic,
                        additionalContext = body.additionalContext ?: "",
                        language = body.language,
                        proficiencyLevel = proficiency.level,
                        goal = body.goal,
                        aiTone = body.tone,
                        aiResponseLength = body.aiResponseLength,
                        user = user,
                        userId = user.id
                    )
                )
            }
            .map { conversationEntity ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(conversationEntity.toDTO())
            }
    }

    override fun getManyConversations(user: UserEntity): Mono<ResponseEntity<List<ConversationDTO>>> {
        return conversationService.findAll(user.id)
            .map { it.toDTO() }
            .collectList()
            .map { conversationDTOs ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(conversationDTOs)
            }
    }

    override fun getConversationById(
        user: UserEntity,
        conversationId: UUID
    ): Mono<ResponseEntity<ConversationDTO>> {
        return conversationService.findByIdOrFail(
            id = conversationId,
            userId = user.id
        )
            .map { conversationEntity ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(conversationEntity.toDTO())
            }
    }

    override fun deleteConversation(
        user: UserEntity,
        conversationId: UUID
    ): Mono<ResponseEntity<Unit>> {
        return conversationService.deleteById(
            id = conversationId,
            userId = user.id,
        )
            .then(Mono.fromCallable {
                ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build<Unit>()
            })
    }
}