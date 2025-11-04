package com.ord.features.conversation.api.facades.impl

import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.features.conversation.api.facades.ConversationCRUDFacade
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.models.conversation.ConversationDTO
import com.ord.features.conversation.models.conversation.ConversationEntity
import com.ord.features.conversation.models.conversation.ConversationMapper
import com.ord.features.conversation.services.ConversationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

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
        userId: UUID,
        body: CreateConversationRequest
    ): Mono<ResponseEntity<ConversationDTO>> {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(userId, body.language)
            .flatMap { proficiency ->
                conversationService.save(
                    ConversationEntity(
                        topic = body.topic,
                        additionalContext = body.additionalContext ?: "",
                        language = body.language,
                        proficiencyLevel = proficiency.level,
                        type = body.type,
                        aiTone = body.tone,
                        aiInterlocutorName = body.aiInterlocutorName,
                        aiInterlocutorAvatarId = body.aiInterlocutorAvatarId,
                        userId = userId,
                    )
                )
            }
            .map { conversationEntity ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(conversationEntity.toDTO())
            }
    }

    override fun getManyConversations(
        userId: UUID
    ): Mono<ResponseEntity<List<ConversationDTO>>> {
        return conversationService
            .findAll(userId)
            .map { it.toDTO() }
            .collectList()
            .map { conversationDTOs ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(conversationDTOs)
            }
    }

    override fun getConversationById(
        userId: UUID,
        conversationId: UUID
    ): Mono<ResponseEntity<ConversationDTO>> {
        return conversationService
            .findByIdOrFailWithMessages(
                id = conversationId,
                userId = userId
            )
            .map { conversation ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(conversation)
            }
    }

    override fun deleteConversation(
        userId: UUID,
        conversationId: UUID
    ): Mono<ResponseEntity<Unit>> {
        return conversationService.deleteById(
            id = conversationId,
            userId = userId,
        )
            .then(Mono.fromCallable {
                ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build<Unit>()
            })
    }
}