package com.ord.features.conversation.api.facades.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.models.entities.ConversationMessageEntity
import com.ord.features.conversation.models.enums.ConversationMessageSender
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
    override fun initializeConversation() {
        TODO("Not yet implemented")
    }

    override fun requestAIMessage(
        user: UserEntity,
        conversationId: UUID,
        lastestUserMessage: String
    ): Flux<String> {
        val conversation = conversationService.findByIdOrFail(conversationId, user.id)

        val serializedConversationHistory: List<String> =
            conversation.messages
                .mapIndexed { index, message -> message.serialize(index) }
                .toMutableList()
                .apply {
                    add(
                        serializeMessage(
                            index = this.size,
                            sender = ConversationMessageSender.USER,
                            message = lastestUserMessage
                        )
                    )
                }


        val prompt = Prompt(
            variant = AvailablePrompts.CONVERSATION_REQUEST_AI_RESPONSE,
            params = mapOf(
                "language" to conversation.language.toString(),
                "level" to conversation.proficiencyLevel.toString(),
                "topic" to conversation.topic,
                "goal" to conversation.goal.toString(),
                "goalExplanation" to conversation.goal.contextForAI,
                "additionalContext" to conversation.additionalContext,
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
                        messageOrder = 0
                    )
                }
            )
            .asFlux()
    }

    override fun saveUserMessageAndGetFeedback(
        user: UserEntity,
        conversationId: UUID,
        userMessage: String,
        latestAIMessage: String
    ): ResponseEntity<ReviewedUserConversationMessage> {
        val conversation = conversationService.findByIdOrFail(conversationId, user.id)

        val prompt = Prompt(
            variant = AvailablePrompts.CONVERSATION_REVIEW_USER_RESPONSE,
            params = mapOf(
                "language" to conversation.language.toString(),
                "level" to conversation.proficiencyLevel.toString(),
                "topic" to conversation.topic,
                "goal" to conversation.goal.toString(),
                "goalExplanation" to conversation.goal.contextForAI,
                "additionalContext" to conversation.additionalContext,
                "userMessage" to userMessage,
                "latestAIMessage" to latestAIMessage
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