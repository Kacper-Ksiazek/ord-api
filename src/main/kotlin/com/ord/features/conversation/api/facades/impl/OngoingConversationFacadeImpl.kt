package com.ord.features.conversation.api.facades.impl

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.OngoingConversationFacade
import com.ord.features.conversation.models.dto.ConversationMessageDTO
import com.ord.features.conversation.models.enums.ConversationMessageSender
import com.ord.features.conversation.services.ConversationService
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.*

@Service
class OngoingConversationFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val conversationService: ConversationService
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

        val prompt = Prompt(
            variant = AvailablePrompts.CONVERSATION_REQUEST_AI_RESPONSE,
            params = mapOf(
                "language" to conversation.language.toString(),
                "level" to conversation.proficiencyLevel.toString(),
                "topic" to conversation.topic,
                "goal" to conversation.goal.toString(),
                "goalExplanation" to conversation.goal.contextForAI,
                "serializedConversationHistory" to listOf<String>().toParamString()
            )
        )

        return openAIAPIClientService
            .openSimpleStringStream(
                prompt = prompt.toString(),
                onComplete = { (payload, emitter) ->
                    // TODO: Save logs here

                    // TODO: Save AI message here - but first add extension function to the ConversationEntity which would simply allow adding new messages to it
                }
            )
            .asFlux()
    }

    override fun saveUserMessageAndGetFeedback() {
        TODO("Not yet implemented")
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