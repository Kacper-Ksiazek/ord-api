package com.ord.features.conversation.api.facades.impl

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.ai_provider.dto.helpers.SimpleStreamedArrayItem
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.ConversationTopicFacade
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import com.ord.features.conversation.services.ConversationService
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ConversationTopicFacadeImpl(
    private val env: Environment,
    private val conversationService: ConversationService,
    private val openAIStreamClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService
) : ConversationTopicFacade {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)

    val isTestingEnv: Boolean = env.activeProfiles.contains("test")

    override fun suggestTopics(user: UserEntity, body: SuggestConversationTopicRequest): Flux<String> {
        val languageProficiency = languageProficiencyService.findUserProficiencyInLanguageOrThrow(
            user.id,
            body.language
        )

        val recentTopics = conversationService.findRecentTopics(
            userId = user.id,
            language = body.language,
            limit = 10,
            goal = body.conversationGoal
        )

        val prompt = Prompt(
            variant = AvailablePrompts.CONVERSATION_SUGGEST_TOPIC,
            params = mapOf(
                "language" to body.language.toString(),
                "level" to languageProficiency.proficiency.toString(),
                "clue" to (body.clueFromUser ?: "NONE"),
                "goal" to body.conversationGoal.toString(),
                "goalExplanation" to body.conversationGoal.contextForAI,
                "examples" to body.conversationGoal.examplesForAI.toParamString(tabulated = true),
                "recentConversations" to recentTopics.toParamString(tabulated = true),
                "separator" to OpenAIAPIClientService.STREAMING_CONTENT_SEPARATOR
            )
        )

        val flux = openAIStreamClientService
            .openStructuredArrayStream<SimpleStreamedArrayItem>(
                prompt = prompt.toString(),
                streamedItemType = object : TypeReference<SimpleStreamedArrayItem>() {},
                onComplete = { (payload, emitter) ->
                    emitter.tryEmitNext(
                        objectMapper.writeValueAsString(
                            payload.finalContent
                        )
                    )
                }
            )

        return if (isTestingEnv) {
            // For tests: block until the full result is collected and emit as Flux
            @Suppress("BlockingMethodInNonBlockingContext")
            Flux.fromIterable(flux.collectList().block() ?: emptyList())
        } else {
            // For production: stream each chunk
            flux
        }
    }
}