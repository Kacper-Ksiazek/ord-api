package com.ord.features.ai_explainer.api.facades.impl

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.ai_provider_usage.models.AiProviderUsageOperationType
import com.ord.core.ai_provider_usage.services.AiProviderUsageService
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserDTO
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.ai_explainer.api.facades.AIExplainerFacade
import com.ord.features.ai_explainer.api.requests.ExplainPhraseAutocompleteRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseFollowUpRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.features.ai_explainer.api.responses.ExplainPhraseAutocompleteResponse
import com.ord.features.ai_explainer.model.ai.OpenAIExplainPhraseAutocomplete
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tools.jackson.core.type.TypeReference
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class AIExplainerFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
    private val aiProviderUsageService: AiProviderUsageService,
) : AIExplainerFacade {
    private val logger = LoggerFactory.getLogger(AIExplainerFacadeImpl::class.java)

    private companion object {
        const val AUTOCOMPLETE_MAX_LENGTH = 120
    }

    override fun explainPhrase(
        body: ExplainPhraseRequest,
        user: UserDTO,
    ): Flux<String> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMapMany { userProficiencyInRequestedLanguage ->
                val translateTo: LanguageName = userProficiencyInRequestedLanguage.translateTo
                val proficiencyLevel: LanguageProficiencyLevel = userProficiencyInRequestedLanguage.level

                val prompt = Prompt(
                    variant = AvailablePrompts.WORDS_EXPLAIN,
                    params = mapOf(
                        "word" to body.phrase,
                        "wordLanguage" to body.language.toString(),
                        "translationLanguage" to translateTo.toString(),
                        "proficiency" to proficiencyLevel.toString(),
                        "generativeContentLanguage" to userProficiencyInRequestedLanguage.generativeContentLanguage.toString(),
                        "additionalContext" to (body.context ?: "Not provided"),
                        "customInstruction" to (body.customInstruction ?: "Not provided")
                    )
                ).toString()

                openAIAPIClientService.openSimpleStringStream(
                    prompt = prompt,
                    userId = user.id,
                    gptTokensUsageLogKey = AiProviderUsageOperationType.AIExplainer.EXPLAIN_PHRASE,
                    onComplete = { (payload, emitter) ->
                        emitter.tryEmitComplete()
                    }
                )
            }
    }

    override fun followUpExplainPhrase(
        body: ExplainPhraseFollowUpRequest,
        user: UserDTO,
    ): Flux<String> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMapMany { userProficiencyInRequestedLanguage ->
                val translateTo: LanguageName = userProficiencyInRequestedLanguage.translateTo
                val proficiencyLevel: LanguageProficiencyLevel = userProficiencyInRequestedLanguage.level

                val prompt = Prompt(
                    variant = body.action.prompt,
                    params = mapOf(
                        "word" to body.phrase,
                        "wordLanguage" to body.language.toString(),
                        "translationLanguage" to translateTo.toString(),
                        "proficiency" to proficiencyLevel.toString(),
                        "generativeContentLanguage" to userProficiencyInRequestedLanguage.generativeContentLanguage.toString(),
                        "previousExplanation" to body.previousExplanation,
                        "existingExamples" to existingExampleLines(body.previousExplanation),
                        "additionalContext" to (body.context ?: "Not provided"),
                    ),
                ).toString()

                openAIAPIClientService.openSimpleStringStream(
                    prompt = prompt,
                    userId = user.id,
                    gptTokensUsageLogKey = body.action.operationKey,
                    onComplete = { (_, emitter) ->
                        emitter.tryEmitComplete()
                    },
                )
            }
    }

    override fun autocompletePhrase(
        body: ExplainPhraseAutocompleteRequest,
        user: UserDTO,
    ): Mono<ExplainPhraseAutocompleteResponse> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMap { userProficiencyInRequestedLanguage ->
                val prompt = Prompt(
                    variant = AvailablePrompts.AI_EXPLAINER_AUTOCOMPLETE,
                    params = mapOf(
                        "prefix" to body.prefix,
                        "language" to body.language.toString(),
                        "proficiency" to userProficiencyInRequestedLanguage.level.toString(),
                        "translationLanguage" to userProficiencyInRequestedLanguage.translateTo.toString(),
                    ),
                )

                openAIAPIClientService.makeRequest(
                    aiResponseType = object : TypeReference<OpenAIExplainPhraseAutocomplete>() {},
                    prompt = prompt,
                    userId = user.id,
                    gptTokensUsageLogKey = AiProviderUsageOperationType.AIExplainer.AUTOCOMPLETE,
                    // Tab completions fire on almost every pause in typing and cost a handful of tokens each,
                    // so they are intentionally kept out of the usage log.
                    trackUsage = false,
                )
            }
            .map { ExplainPhraseAutocompleteResponse(completion = sanitizeCompletion(body.prefix, it.completion)) }
    }

    /**
     * Keeps only a short, single-line continuation. Models sometimes echo the typed prefix back,
     * so a repeated prefix is stripped instead of being appended twice.
     */
    private fun sanitizeCompletion(prefix: String, completion: String): String {
        val singleLine = completion.lineSequence().firstOrNull().orEmpty().trimEnd()
        val withoutEcho = if (singleLine.startsWith(prefix, ignoreCase = true)) {
            singleLine.substring(prefix.length)
        } else {
            singleLine
        }

        return withoutEcho.take(AUTOCOMPLETE_MAX_LENGTH)
    }

    private fun existingExampleLines(previousExplanation: String): String {
        val lines = previousExplanation
            .lineSequence()
            .map { it.trim() }
            .filter { it.startsWith("»") }
            .toList()

        return if (lines.isEmpty()) "None yet" else lines.joinToString("\n")
    }
}