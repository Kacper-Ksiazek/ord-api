package com.ord.features.conversation.api.facades.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.models.GptTokensUsageOperationType
import com.ord.core.gpt_tokens_usage.services.GptTokensUsageService
import com.ord.exceptions.REST.BadRequestException
import org.slf4j.LoggerFactory
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
import com.ord.features.conversation.models.conversation_message.ConversationMessageDTO
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.models.conversation.extensions.convertToPromptParams
import com.ord.features.conversation.models.conversation.ConversationMapper
import com.ord.features.conversation.models.conversation_user_message_feedback.enums.ErrorType
import com.ord.features.conversation.services.ConversationMessageService
import com.ord.shared.utils.EnumUtils.joinEnumValues
import com.ord.features.conversation.services.ConversationService
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
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
    private val conversationMapper: ConversationMapper,
    private val gptTokensUsageService: GptTokensUsageService,
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
                        onComplete = { (payload) ->
                            gptTokensUsageService.saveTokensUsage(
                                userId = userId,
                                operationType = GptTokensUsageOperationType.Conversation.INITIALIZE,
                                model = "gpt-4.1-mini",
                                inputTokens = payload.inputTokens,
                                outputTokens = payload.outputTokens
                            ).subscribe(
                                { /* success */ },
                                { error -> logger.error("Failed to log token usage for conversation initialization", error) }
                            )

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
                        onComplete = { (payload, emitter) ->
                            gptTokensUsageService.saveTokensUsage(
                                userId = userId,
                                operationType = GptTokensUsageOperationType.Conversation.AI_RESPONSE,
                                model = "gpt-4.1-mini",
                                inputTokens = payload.inputTokens,
                                outputTokens = payload.outputTokens
                            ).subscribe(
                                { /* success */ },
                                { error -> logger.error("Failed to log token usage for conversation AI response", error) }
                            )

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


    override fun saveUserMessageAndGetFeedback(
        userId: UUID,
        body: ReviewUserConversationMessageRequest
    ): Mono<ResponseEntity<ReviewedUserConversationMessage>> {
        return conversationService
            .findByIdOrFail(body.conversationId, userId)
            .map { conversationMapper.toDTO(it) }
            .flatMap { conversation ->
                val prompt = Prompt(
                    variant = AvailablePrompts.CONVERSATION_REVIEW_USER_RESPONSE,
                    params = conversation.convertToPromptParams() + mapOf(
                        "userMessage" to body.message,
                        "latestAIMessage" to (body.latestAIMessage
                            ?: "NO PREVIOUS MESSAGES. This message is the first one in the conversation."),
                        "errorTypes" to ErrorType::class.joinEnumValues(separator = " | ").split(" | ").joinToString(" | ") { "\"$it\"" },
                    )
                )

                openAIAPIClientService.makeRequest(
                    prompt = prompt.toString(),
                    aiResponseType = object : TypeReference<ReviewedUserConversationMessage>() {},
                    saveLog = { openAIResponse ->
                        gptTokensUsageService.saveTokensUsage(
                            userId = userId,
                            operationType = GptTokensUsageOperationType.Conversation.REVIEW_USER_MESSAGE,
                            model = "gpt-4.1-mini",
                            inputTokens = openAIResponse.usage.input_tokens,
                            outputTokens = openAIResponse.usage.output_tokens
                        ).subscribe(
                            { /* success */ },
                            { error -> logger.error("Failed to log token usage for conversation user message review", error) }
                        )
                    }
                )
                    .flatMap { aiFeedback ->
                        conversationMessageService.createMessageWithFeedback(
                            conversationId = conversation.id,
                            content = body.message,
                            messageOrder = body.messageOrder,
                            aiFeedback = aiFeedback
                        )
                            .then(Mono.fromCallable {
                                ResponseEntity.ok(aiFeedback)
                            })
                    }
            }
    }

    //
    // Utility functions
    //

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