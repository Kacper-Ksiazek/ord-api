package com.ord.features.conversation.api.facades.impl

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.ai_provider.dto.helpers.StreamSimpleItem
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.features.conversation.api.facades.ConversationTopicFacade
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import com.ord.features.conversation.services.ConversationService
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ConversationTopicFacadeImpl(
    private val conversationService: ConversationService,
    private val openAIStreamClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService
) : ConversationTopicFacade {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)

    override fun suggestTopics(user: UserEntity, body: SuggestConversationTopicRequest): Flux<String> {
        return languageProficiencyService.findUserProficiencyInLanguageOrThrow(user.id, body.language)
            .zipWith(
                conversationService.findRecentTopics(
                    userId = user.id,
                    language = body.language,
                    limit = 10,
                    goal = body.conversationGoal
                ).collectList()
            )
            .flatMapMany { tuple ->
                val languageProficiency = tuple.t1
                val recentTopics = tuple.t2
                val prompt = Prompt(
                    variant = AvailablePrompts.CONVERSATION_SUGGEST_TOPIC,
                    params = mapOf(
                        "language" to body.language.toString(),
                        "level" to languageProficiency.level.toString(),
                        "clue" to (body.clueFromUser ?: "NONE"),
                        "goal" to body.conversationGoal.toString(),
                        "goalExplanation" to body.conversationGoal.contextForAI,
                        "examples" to body.conversationGoal.examplesForAI.toParamString(tabulated = true),
                        "recentConversations" to recentTopics.toParamString(tabulated = true),
                        "separator" to OpenAIAPIClientService.STREAMING_CONTENT_SEPARATOR
                    )
                )

                openAIStreamClientService
                    .openStructuredArrayStream<StreamSimpleItem>(
                        prompt = prompt.toString(),
                        streamedItemType = object : TypeReference<StreamSimpleItem>() {},
                        onComplete = { (payload, emitter) ->
                            emitter.tryEmitNext(
                                objectMapper.writeValueAsString(
                                    payload.finalContent
                                )
                            )
                        }
                    )
            }
    }
}