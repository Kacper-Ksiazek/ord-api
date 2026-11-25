package com.ord.features.ai_explainer.api.facades.impl

import com.ord.core.ai_provider.services.Emitter
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
import com.ord.features.ai_explainer.api.requests.ExplainPhraseEnhanceRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseFollowUpRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseSuggestActionsRequest
import com.ord.features.ai_explainer.api.responses.ExplainPhraseAutocompleteResponse
import com.ord.features.ai_explainer.api.responses.ExplainPhraseEnhanceResponse
import com.ord.features.ai_explainer.api.responses.ExplainPhraseSuggestActionsResponse
import com.ord.features.ai_explainer.model.ai.OpenAIExplainPhraseAutocomplete
import com.ord.features.ai_explainer.model.ai.OpenAIExplainPhraseEnhancePrompt
import com.ord.features.ai_explainer.model.ai.OpenAIExplainPhraseSuggestActions
import com.ord.features.ai_explainer.models.explanation.AIExplanationDTO
import com.ord.features.ai_explainer.services.AIExplanationService
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import tools.jackson.core.type.TypeReference
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class AIExplainerFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
    private val aiProviderUsageService: AiProviderUsageService,
    private val aiExplanationService: AIExplanationService,
) : AIExplainerFacade {
    private val logger = LoggerFactory.getLogger(AIExplainerFacadeImpl::class.java)

    private companion object {
        const val AUTOCOMPLETE_MAX_LENGTH = 120
        const val ENHANCED_PROMPT_MAX_LENGTH = 255
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
                        persistThenComplete(emitter) {
                            body.explanationId?.let { id ->
                                aiExplanationService.create(
                                    id = id,
                                    userId = user.id,
                                    phrase = body.phrase,
                                    language = body.language,
                                    explanation = payload.finalContent
                                )
                            }
                        }
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
                    onComplete = { (payload, emitter) ->
                        persistThenComplete(emitter) {
                            body.explanationId?.let { id ->
                                aiExplanationService.updateFollowUp(
                                    id = id,
                                    userId = user.id,
                                    action = body.action,
                                    text = payload.finalContent
                                )
                            }
                        }
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

    override fun enhancePrompt(
        body: ExplainPhraseEnhanceRequest,
        user: UserDTO,
    ): Mono<ExplainPhraseEnhanceResponse> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMap { userProficiencyInRequestedLanguage ->
                val prompt = Prompt(
                    variant = AvailablePrompts.AI_EXPLAINER_ENHANCE_PROMPT,
                    params = mapOf(
                        "prompt" to body.prompt,
                        "language" to body.language.toString(),
                        "proficiency" to userProficiencyInRequestedLanguage.level.toString(),
                        "translationLanguage" to userProficiencyInRequestedLanguage.translateTo.toString(),
                    ),
                )

                openAIAPIClientService.makeRequest(
                    aiResponseType = object : TypeReference<OpenAIExplainPhraseEnhancePrompt>() {},
                    prompt = prompt,
                    userId = user.id,
                    gptTokensUsageLogKey = AiProviderUsageOperationType.AIExplainer.ENHANCE_PROMPT,
                )
            }
            .map {
                // A blank result must never wipe what the learner typed, so fall back to their own text.
                val enhanced = it.prompt.trim().take(ENHANCED_PROMPT_MAX_LENGTH).ifBlank { body.prompt }

                ExplainPhraseEnhanceResponse(prompt = enhanced)
            }
    }

    override fun suggestActions(
        body: ExplainPhraseSuggestActionsRequest,
        user: UserDTO,
    ): Mono<ExplainPhraseSuggestActionsResponse> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMap { userProficiencyInRequestedLanguage ->
                val prompt = Prompt(
                    variant = AvailablePrompts.AI_EXPLAINER_SUGGEST_ACTIONS,
                    params = mapOf(
                        "phrase" to body.phrase,
                        "language" to body.language.toString(),
                        "explanation" to body.explanation,
                        "proficiency" to userProficiencyInRequestedLanguage.level.toString(),
                        "translationLanguage" to userProficiencyInRequestedLanguage.translateTo.toString(),
                    ),
                )

                openAIAPIClientService.makeRequest(
                    aiResponseType = object : TypeReference<OpenAIExplainPhraseSuggestActions>() {},
                    prompt = prompt,
                    userId = user.id,
                    gptTokensUsageLogKey = AiProviderUsageOperationType.AIExplainer.SUGGEST_ACTIONS,
                    // Small classification call made once per explanation; kept out of the usage log like autocomplete.
                    trackUsage = false,
                )
            }
            .flatMap { result ->
                val response = ExplainPhraseSuggestActionsResponse(
                    kind = result.kind,
                    actions = result.kind.rankActions(result.suggestedActions),
                    emoji = sanitizeEmoji(result.emoji),
                )
                val persist = body.explanationId
                    ?.let { aiExplanationService.updateClassification(
                        id = it,
                        userId = user.id,
                        kind = result.kind,
                        emoji = response.emoji,
                    ) }
                    ?: Mono.empty()

                persist
                    .onErrorResume { error ->
                        logger.warn("Could not persist AI explanation kind", error)
                        Mono.empty()
                    }
                    .thenReturn(response)
            }
    }

    override fun getExplanation(id: UUID, user: UserDTO): Mono<AIExplanationDTO> {
        return aiExplanationService.getById(id = id, userId = user.id)
    }

    override fun getExplanations(user: UserDTO): Mono<ResponseEntity<List<AIExplanationDTO>>> {
        return aiExplanationService
            .findRecent(user.id)
            .collectList()
            .map { ResponseEntity.ok(it) }
    }

    override fun deleteExplanation(
        id: UUID,
        user: UserDTO,
    ): Mono<ResponseEntity<Unit>> {
        return aiExplanationService
            .delete(id = id, userId = user.id)
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.NO_CONTENT).build() })
    }

    /**
     * Persists the finished stream before signalling completion, so a client that reacts to the end of
     * the stream (e.g. a delete button) can never overtake the write. A failed write is only logged:
     * the learner has already received the answer.
     */
    private fun persistThenComplete(
        emitter: Emitter,
        persist: () -> Mono<Void>?,
    ) {
        (persist() ?: Mono.empty())
            .onErrorResume { error ->
                logger.warn("Could not persist AI explanation", error)
                Mono.empty()
            }
            .doFinally { emitter.tryEmitComplete() }
            .subscribe()
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

    /** Keeps only the first user-perceived character, and only when it is a real emoji-like symbol, never plain text. */
    private fun sanitizeEmoji(raw: String): String? {
        val trimmed = raw.trim()

        if (trimmed.isEmpty()) {
            return null
        }

        val iterator = java.text.BreakIterator.getCharacterInstance()
        iterator.setText(trimmed)
        val first = trimmed.substring(0, iterator.next())

        return first.takeIf { cluster -> cluster.codePoints().anyMatch { it > 0x2000 } }
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