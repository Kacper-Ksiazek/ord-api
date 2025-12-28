package com.ord.features.conversation.api.facades.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.models.GptTokensUsageOperationType
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.exceptions.REST.BadRequestException
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.api.facades.helpers.ai_responses.AIMessageLearningTips
import com.ord.features.conversation.api.facades.helpers.ai_responses.OpenAIAIMessageLearningTips
import com.ord.features.conversation.api.facades.helpers.ai_responses.OpenAIReviewedMessage
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.GetFeedbackOnUserConversationMessageRequest
import com.ord.features.conversation.api.requests.GetLearningTipsForAIMessageRequest
import com.ord.features.conversation.api.requests.SaveUserConversationMessageRequest
import com.ord.features.conversation.models.conversation.ConversationMapper
import com.ord.features.conversation.models.conversation.extensions.convertToPromptParams
import com.ord.features.conversation.models.conversation_message.ConversationMessageDTO
import com.ord.features.conversation.models.conversation_message.ConversationMessageMapper
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.services.ConversationMessageService
import com.ord.features.conversation.services.ConversationService
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class OngoingConversationFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val conversationService: ConversationService,
    private val conversationMessageService: ConversationMessageService,
    private val conversationMessageRepository: com.ord.features.conversation.repositories.ConversationMessageRepository,
    private val conversationMapper: ConversationMapper,
    private val conversationMessageMapper: ConversationMessageMapper,
    private val languageProficiencyService: LanguageProficiencyService,
) : OngoingConversationFacade {
    private val logger = LoggerFactory.getLogger(OngoingConversationFacadeImpl::class.java)
    override fun initializeConversationByAI(
        conversationId: UUID,
        userId: UUID,
    ): Flux<String> {
        return conversationService
            .findByIdOrFailWithMessages(
                id = conversationId,
                userId = userId
            )
            .flatMapMany { conversation ->
                if (conversation.messages.isNotEmpty()) {
                    return@flatMapMany Flux.error(
                        BadRequestException("Conversation with id ${conversation.id} has been already initialized.")
                    )
                }

                val prompt = Prompt(
                    variant = AvailablePrompts.CONVERSATION_INITIALIZE,
                    params = conversation.convertToPromptParams()
                )

                openAIAPIClientService
                    .openSimpleStringStream(
                        prompt = prompt.toString(),
                        userId = userId,
                        gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.INITIALIZE,
                        onComplete = { (payload) ->
                            conversationMessageService.createMessage(
                                conversationId = conversation.id,
                                sender = ConversationMessageSender.AI,
                                content = payload.finalContent,
                                messageOrder = 0
                            ).subscribe()
                        }
                    )
            }
    }


    override fun requestAIMessage(
        userId: UUID,
        body: CreateAIConversationMessageRequest
    ): Flux<String> {
        return conversationService
            .findByIdOrFailWithMessages(body.conversationId, userId)
            .flatMapMany { conversation ->
                val serializedConversationHistory: List<String> =
                    conversation.messages
                        .mapIndexed { index, message -> message.serialize(index) }
                        .toMutableList()
                        .apply {
                            add(
                                serializeMessage(
                                    index = this.size,
                                    sender = ConversationMessageSender.USER,
                                    message = body.latestUserMessage
                                )
                            )
                        }

                val prompt = Prompt(
                    variant = AvailablePrompts.CONVERSATION_REQUEST_AI_RESPONSE,
                    params = conversation.convertToPromptParams() + mapOf(
                        "serializedConversationHistory" to serializedConversationHistory.toParamString(tabulated = true),
                    )
                )

                openAIAPIClientService
                    .openSimpleStringStream(
                        prompt = prompt.toString(),
                        userId = userId,
                        gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.AI_RESPONSE,
                        onComplete = { (payload, emitter) ->
                            conversationMessageService.createMessage(
                                conversationId = conversation.id,
                                sender = ConversationMessageSender.AI,
                                content = payload.finalContent,
                                messageOrder = body.messageOrder
                            ).subscribe()
                        }
                    )
            }
    }

    override fun saveUserMessage(
        userId: UUID,
        body: SaveUserConversationMessageRequest
    ): Mono<ResponseEntity<ConversationMessageDTO>> {
        return conversationService
            .findByIdOrFail(body.conversationId, userId)
            .flatMap {
                conversationMessageService.saveUserMessageWithId(
                    messageId = body.messageId,
                    conversationId = body.conversationId,
                    content = body.content,
                    messageOrder = body.messageOrder
                )
            }
            .map { entity -> conversationMessageMapper.toDTO(entity) }
            .map { dto -> ResponseEntity.ok(dto) }
    }

    override fun generateFeedbackForMessage(
        userId: UUID,
        body: GetFeedbackOnUserConversationMessageRequest
    ): Mono<ResponseEntity<ReviewedUserConversationMessage>> {
        return conversationService
            .findByIdOrFail(body.conversationId, userId)
            .map { conversationMapper.toDTO(it) }
            .flatMap { conversation ->
                conversationMessageRepository.findById(body.messageId)
                    .switchIfEmpty(Mono.error(NotFoundException("Message with id ${body.messageId} not found")))
                    .map { userMessage -> Pair(conversation, userMessage) }
            }
            .flatMap { (conversation, userMessage) ->
                // Get user's language proficiency for generativeContentLanguage
                languageProficiencyService.findUserProficiencyInLanguageOrThrow(
                    userId = userId,
                    languageName = conversation.language
                )
                    .flatMap { proficiency ->
                        val prompt = Prompt(
                            variant = AvailablePrompts.CONVERSATION_REVIEW_USER_RESPONSE,
                            params = conversation.convertToPromptParams() + mapOf(
                                "userMessage" to userMessage.content,
                                "latestAIMessage" to (body.latestAIMessage
                                    ?: "NO PREVIOUS MESSAGES. This message is the first one in the conversation."),
                                "generativeContentLanguage" to proficiency.generativeContentLanguage.toString()
                            )
                        )

                        openAIAPIClientService.makeRequest(
                            prompt = prompt.toString(),
                            aiResponseType = object : TypeReference<OpenAIReviewedMessage>() {},
                            userId = userId,
                            gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.REVIEW_USER_MESSAGE,
                            structuredOutput = prompt.variant.structuredOutput,
                        )
                            .map { openAIResponse -> openAIResponse.toDomain() }
                            .flatMap { aiFeedback ->
                                conversationMessageService.saveFeedbackForExistingMessage(
                                    messageId = body.messageId,
                                    aiFeedback = aiFeedback
                                )
                                    .then(Mono.fromCallable {
                                        ResponseEntity.ok(aiFeedback)
                                    })
                            }
                    }
            }
    }

    override fun generateLearningTipsForAIMessage(
        userId: UUID,
        body: GetLearningTipsForAIMessageRequest
    ): Mono<ResponseEntity<AIMessageLearningTips>> {
        return conversationService
            .findByIdOrFail(body.conversationId, userId)
            .map { conversationMapper.toDTO(it) }
            .flatMap { conversation ->
                conversationMessageRepository.findLatestAIMessageWithoutLearningTips(body.conversationId)
                    .switchIfEmpty(Mono.error(NotFoundException("No AI message without learning tips found in conversation ${body.conversationId}")))
                    .map { aiMessage -> Pair(conversation, aiMessage) }
            }
            .flatMap { (conversation, aiMessage) ->
                // Get user's language proficiency for generativeContentLanguage
                languageProficiencyService.findUserProficiencyInLanguageOrThrow(
                    userId = userId,
                    languageName = conversation.language
                )
                    .flatMap { proficiency ->
                        val prompt = Prompt(
                            variant = AvailablePrompts.CONVERSATION_GENERATE_AI_MESSAGE_LEARNING_TIPS,
                            params = conversation.convertToPromptParams() + mapOf(
                                "aiMessage" to aiMessage.content,
                                "generativeContentLanguage" to proficiency.generativeContentLanguage.toString()
                            )
                        )

                        openAIAPIClientService.makeRequest(
                            prompt = prompt,
                            aiResponseType = object : TypeReference<OpenAIAIMessageLearningTips>() {},
                            userId = userId,
                            gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.GENERATE_AI_MESSAGE_LEARNING_TIPS,
                        )
                            .map { openAIResponse -> openAIResponse.toDomain() }
                            .flatMap { learningTips ->
                                conversationMessageService.saveLearningTipsForExistingMessage(
                                    messageId = aiMessage.id!!,
                                    learningTips = learningTips
                                )
                                    .then(Mono.fromCallable {
                                        ResponseEntity.ok(learningTips)
                                    })
                            }
                    }
            }
    }

    private fun ConversationMessageDTO.serialize(index: Int): String {
        return serializeMessage(index, sender, content)
    }

    private fun serializeMessage(
        index: Int,
        sender: ConversationMessageSender,
        message: String
    ): String {
        return "Message $index. - ROLE: $sender; MESSAGE: $message"
    }
}