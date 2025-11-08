package com.ord.features.conversation.api.facades.impl

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.ai_provider.dto.helpers.StreamSimpleItem
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.models.GptTokensUsageOperationType
import com.ord.core.gpt_tokens_usage.services.GptTokensUsageService
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import org.slf4j.LoggerFactory
import com.ord.features.conversation.api.facades.ConversationAuxiliaryFacade
import com.ord.features.conversation.api.facades.helpers.ai_responses.GeneratedAIInterlocutorData
import com.ord.features.conversation.api.requests.GenerateAIInterlocutorDataRequest
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import com.ord.features.conversation.models.conversation.enums.ConversationAIBotAvatar
import com.ord.features.conversation.services.ConversationService
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class ConversationAuxiliaryFacadeImpl(
    private val conversationService: ConversationService,
    private val openAIStreamClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
    private val gptTokensUsageService: GptTokensUsageService,
) : ConversationAuxiliaryFacade {
    private val logger = LoggerFactory.getLogger(ConversationAuxiliaryFacadeImpl::class.java)
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)

    override fun suggestTopics(
        userId: UUID,
        body: SuggestConversationTopicRequest
    ): Flux<String> {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(userId, body.language)
            .zipWith(
                conversationService.findRecentTopics(
                    userId = userId,
                    language = body.language,
                    limit = 10,
                    type = body.conversationType
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
                        "type" to body.conversationType.toString(),
                        "typeExplanation" to body.conversationType.contextForAI,
                        "examples" to body.conversationType.examplesForAI.toParamString(tabulated = true),
                        "recentConversations" to recentTopics.toParamString(tabulated = true),
                        "separator" to OpenAIAPIClientService.STREAMING_CONTENT_SEPARATOR
                    )
                )

                openAIStreamClientService
                    .openStructuredArrayStream(
                        prompt = prompt.toString(),
                        streamedItemType = object : TypeReference<StreamSimpleItem>() {},
                        onComplete = { (payload, emitter) ->
                            gptTokensUsageService.saveTokensUsage(
                                userId = userId,
                                operationType = GptTokensUsageOperationType.Conversation.SUGGEST_TOPICS,
                                model = "gpt-4.1-mini",
                                inputTokens = payload.inputTokens,
                                outputTokens = payload.outputTokens
                            ).subscribe(
                                { /* success */ },
                                { error -> logger.error("Failed to log token usage for conversation topic suggestions", error) }
                            )

                            emitter.tryEmitNext(
                                objectMapper.writeValueAsString(
                                    payload.finalContent
                                )
                            )
                        }
                    )
            }
    }

    override fun generateAIInterlocutorData(
        userId: UUID,
        body: GenerateAIInterlocutorDataRequest
    ): Mono<GeneratedAIInterlocutorData> {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(userId, body.language)
            .flatMap { languageProficiency ->
                val prompt = Prompt(
                    variant = AvailablePrompts.CONVERSATION_GENERATE_AI_INTERLOCUTOR,
                    params = mapOf(
                        "language" to body.language.toString(),
                        "level" to languageProficiency.level.toString(),
                        "topic" to body.topic,
                        "type" to body.conversationType.toString(),
                        "typeExplanation" to body.conversationType.contextForAI,
                        "additionalContext" to (body.additionalContext ?: "NONE"),
                        "availableAvatars" to ConversationAIBotAvatar.toPromptList()
                    )
                )

                openAIStreamClientService
                    .makeRequest(
                        prompt = prompt.toString(),
                        aiResponseType = object : TypeReference<GeneratedAIInterlocutorData>() {},
                        saveLog = { openAIResponse ->
                            gptTokensUsageService.saveTokensUsage(
                                userId = userId,
                                operationType = GptTokensUsageOperationType.Conversation.GENERATE_INTERLOCUTOR,
                                model = "gpt-4.1-mini",
                                inputTokens = openAIResponse.usage.input_tokens,
                                outputTokens = openAIResponse.usage.output_tokens
                            ).subscribe(
                                { /* success */ },
                                { error -> logger.error("Failed to log token usage for generating AI interlocutor", error) }
                            )
                        }
                    )
            }
    }
}