package com.ord.features.conversation.api.facades.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.user.model.UserEntity
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.features.conversation.models.enums.ConversationMessageSender
import com.ord.features.conversation.models.extensions.convertToPromptParams
import com.ord.features.conversation.services.ConversationMessageService
import com.ord.features.conversation.services.ConversationService
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.*

@Service
class OngoingConversationFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val conversationService: ConversationService,
    private val conversationMessageService: ConversationMessageService,
) : OngoingConversationFacade {
    override fun initializeConversationByAI(
        user: UserEntity,
        conversationId: UUID
    ): Flux<String> {
        val conversation = conversationService.findByIdOrFail(conversationId, user.id)

        if (conversation.messages.isNotEmpty()) {
            throw BadRequestException("Conversation with id $conversationId has been already initialized. ")
        }

        val prompt = Prompt(
            variant = AvailablePrompts.CONVERSATION_INITIALIZE,
            params = conversation.convertToPromptParams()
        )

        return openAIAPIClientService
            .openSimpleStringStream(
                prompt = prompt.toString(),
                onComplete = { (payload) ->
                    // TODO: Save logs here

                    conversationMessageService.createMessage(
                        conversationId = conversation.id,
                        sender = ConversationMessageSender.AI,
                        content = payload.finalContent,
                        messageOrder = 0
                    )
                }
            )
            .asFlux()
    }

    override fun requestAIMessage(
        user: UserEntity,
        body: CreateAIConversationMessageRequest
    ): Flux<String> {
        val conversation = conversationService.findByIdOrFail(body.conversationId, user.id)

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

        return openAIAPIClientService
            .openSimpleStringStream(
                prompt = prompt.toString(),
                onComplete = { (payload, emitter) ->
                    // TODO: Save logs here

                    conversationMessageService.createMessage(
                        conversationId = conversation.id,
                        sender = ConversationMessageSender.AI,
                        content = payload.finalContent,
                        messageOrder = body.messageOrder
                    )
                }
            )
            .asFlux()
    }

    override fun saveUserMessageAndGetFeedback(
        user: UserEntity,
        body: ReviewUserConversationMessageRequest
    ): ResponseEntity<ReviewedUserConversationMessage> {
        val conversation = conversationService.findByIdOrFail(body.conversationId, user.id)

        conversationMessageService.createMessage(
            conversationId = conversation.id,
            sender = ConversationMessageSender.USER,
            content = body.message,
            messageOrder = body.messageOrder
        )

        val prompt = Prompt(
            variant = AvailablePrompts.CONVERSATION_REVIEW_USER_RESPONSE,
            params = conversation.convertToPromptParams() + mapOf(
                "userMessage" to body.message,
                "latestAIMessage" to (body.latestAIMessage
                    ?: "NO PREVIOUS MESSAGES. This message is the first one in the conversation."),
            )
        )

        val aiFeedback: ReviewedUserConversationMessage = openAIAPIClientService.makeRequest(
            prompt = prompt.toString(),
            aiResponseTypeReference = object : TypeReference<ReviewedUserConversationMessage>() {},
            saveLog = { openAIResponse ->
                // TODO
            }
        )

        return ResponseEntity.ok(aiFeedback)
    }

    //
    // Utility functions
    //

    private fun ConversationMessageEntity.serialize(index: Int): String {
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