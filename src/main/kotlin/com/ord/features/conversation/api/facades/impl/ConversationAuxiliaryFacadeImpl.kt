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
                val recentTopicsFromDB = tuple.t2

                // Combine DB topics with frontend-provided excluded topics
                val allTopicsToExclude = buildSet {
                    addAll(recentTopicsFromDB)
                    body.excludeTopics?.let { addAll(it) }
                }.toList()

                val prompt = Prompt(
                    variant = AvailablePrompts.CONVERSATION_SUGGEST_TOPIC,
                    params = mapOf(
                        "language" to body.language.toString(),
                        "level" to languageProficiency.level.toString(),
                        "clue" to (body.clueFromUser ?: "NONE"),
                        "type" to body.conversationType.toString(),
                        "typeExplanation" to body.conversationType.contextForAI,
                        "examples" to body.conversationType.examplesForAI.toParamString(tabulated = true),
                        "recentConversations" to allTopicsToExclude.toParamString(tabulated = true),
                        "separator" to OpenAIAPIClientService.STREAMING_CONTENT_SEPARATOR
                    )
                )

                openAIStreamClientService
                    .openStructuredArrayStream(
                        prompt = prompt.toString(),
                        streamedItemType = object : TypeReference<StreamSimpleItem>() {},
                        userId = userId,
                        gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.SUGGEST_TOPICS
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
                val formattedRecentInterlocutors = if (body.recentInterlocutors.isNullOrEmpty()) {
                    "NONE"
                } else {
                    body.recentInterlocutors.joinToString(separator = "\n") { interlocutor ->
                        "- ${interlocutor.name} (Avatar: ${interlocutor.avatarId})"
                    }
                }

                val prompt = Prompt(
                    variant = AvailablePrompts.CONVERSATION_GENERATE_AI_INTERLOCUTOR,
                    params = mapOf(
                        "language" to body.language.toString(),
                        "level" to languageProficiency.level.toString(),
                        "topic" to body.topic,
                        "type" to body.conversationType.toString(),
                        "typeExplanation" to body.conversationType.contextForAI,
                        "additionalContext" to (body.additionalContext ?: "NONE"),
                        "availableAvatars" to ConversationAIBotAvatar.toPromptList(),
                        "recentInterlocutors" to formattedRecentInterlocutors
                    )
                )

                openAIStreamClientService
                    .makeRequest(
                        prompt = prompt.toString(),
                        aiResponseType = object : TypeReference<GeneratedAIInterlocutorData>() {},
                        userId = userId,
                        gptTokensUsageLogKey = GptTokensUsageOperationType.Conversation.GENERATE_INTERLOCUTOR
                    )
            }
    }
}