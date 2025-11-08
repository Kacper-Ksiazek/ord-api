package com.ord.core.word.api.ai.facades.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.models.GptTokensUsageOperationType
import com.ord.core.gpt_tokens_usage.services.GptTokensUsageService
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.ai.facades.WordAIFacade
import org.slf4j.LoggerFactory
import com.ord.core.word.api.ai.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest
import com.ord.core.word.api.ai.responses.dto.AIGeneratedWordManual
import com.ord.core.word.api.ai.responses.dto.VocabularySuggestion
import com.ord.core.word.models.word_details.enums.WordCollocationFrequency
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word_details.enums.WordGender
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.services.WordService
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.quickly_added_words.repositories.QAWRepository
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.utils.EnumUtils.joinEnumValues
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class WordAIFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
    private val wordService: WordService,
    private val qawRepository: QAWRepository,
    private val gptTokensUsageService: GptTokensUsageService,
) : WordAIFacade {
    private val logger = LoggerFactory.getLogger(WordAIFacadeImpl::class.java)
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    override fun generateWordManual(
        body: GenerateWordManualRequest,
        user: UserDTO,
    ): Mono<AIGeneratedWordManual> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMap { userProficiencyInRequestedLanguage ->
                val translateTo: LanguageName =
                    body.targetLanguage ?: userProficiencyInRequestedLanguage!!.translateTo
                val proficiencyLevel: LanguageProficiencyLevel =
                    body.proficiencyLevel ?: userProficiencyInRequestedLanguage!!.level

                val prompt = Prompt(
                    variant = AvailablePrompts.WORDS_GENERATE_MANUAL,
                    params = mapOf(
                        "word" to body.word,
                        "wordLanguage" to body.language.toString(),
                        "desiredLanguage" to translateTo.toString(),
                        "proficiency" to proficiencyLevel.toString(),
                        "generativeContentLanguage" to userProficiencyInRequestedLanguage!!.generativeContentLanguage.toString(),

                        "wordTypes" to WordType::class.joinEnumValues(separator = " | "),
                        "wordExtraMarks" to WordExtraMark::class.joinEnumValues(separator = " | "),
                        "wordCollocationFrequency" to WordCollocationFrequency::class.joinEnumValues(separator = " | "),
                        "wordGenders" to WordGender::class.joinEnumValues(separator = " | ")
                    )
                ).toString()

                openAIAPIClientService.makeRequest(
                    aiResponseType = object : TypeReference<AIGeneratedWordManual>() {},
                    prompt = prompt,
                    saveLog = { openAIResponse ->
                        gptTokensUsageService.saveTokensUsage(
                            userId = user.id,
                            operationType = GptTokensUsageOperationType.Words.GENERATE_MANUAL,
                            model = "gpt-4.1-mini",
                            inputTokens = openAIResponse.usage.input_tokens,
                            outputTokens = openAIResponse.usage.output_tokens
                        ).subscribe(
                            { /* success */ },
                            { error -> logger.error("Failed to log token usage for word manual generation", error) }
                        )
                    },
                    validateResponseBody = { responseBody ->
                        responseBody != null &&
                                !responseBody.toString().contains("NON_EXISTENT_WORD")
                    },
                    parseResponseBody = { responseBody ->
                        when {
                            responseBody.toString().contains("NON_EXISTENT_WORD") ->
                                throw BadRequestException("The word ${body.word} does not exist in the language ${body.language}.")

                            else -> {
                                responseBody.originalWord = body.word
                                responseBody
                            }
                        }
                    }
                )
            }
    }

    override fun suggestVocabulary(
        body: SuggestVocabularyRequest,
        user: UserDTO,
    ): Flux<String> {
        return languageProficiencyService
            .findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(
                Mono.error(
                    BadRequestException("User does not have any proficiency in the requested language.")
                )
            )
            .zipWith(
                // Gather existing vocabulary (both regular words and quickly added words)
                wordService.getWordsForPromptGeneration(
                    userId = user.id,
                    language = body.language,
                    amountOfLatestWord = 1000,
                    amountOfProblematicWord = 0
                ).zipWith(
                    qawRepository
                        .findAllWordsByUserIdAndLanguage(user.id, body.language)
                        .collectList()
                        .map { it.toSet() }
                )
            )
            .flatMapMany { tuple ->
                val userProficiencyInRequestedLanguage = tuple.t1
                val wordsTuple = tuple.t2
                val wordsFromWords = wordsTuple.t1
                val wordsFromQAW = wordsTuple.t2

                val translateTo: LanguageName = userProficiencyInRequestedLanguage!!.translateTo
                val proficiencyLevel: LanguageProficiencyLevel = userProficiencyInRequestedLanguage.level
                val wordCount: Int = 10

                val allExistingWords = wordsFromWords + wordsFromQAW
                val existingWordsString = if (allExistingWords.isEmpty()) {
                    "No existing vocabulary"
                } else {
                    allExistingWords.joinToString(", ")
                }

                val excludedWordsString = if (body.excludedWords.isNullOrEmpty()) {
                    "No previously suggested words"
                } else {
                    body.excludedWords.joinToString(", ")
                }

                val prompt = Prompt(
                    variant = AvailablePrompts.WORDS_SUGGEST_VOCABULARY,
                    params = mapOf(
                        "targetLanguage" to body.language.toString(),
                        "translationLanguage" to translateTo.toString(),
                        "proficiency" to proficiencyLevel.toString(),
                        "generativeContentLanguage" to userProficiencyInRequestedLanguage.generativeContentLanguage.toString(),
                        "userContext" to (body.context ?: "Not specified"),
                        "existingWords" to existingWordsString,
                        "excludedWords" to excludedWordsString,
                        "wordCount" to wordCount.toString(),
                        "separator" to OpenAIAPIClientService.STREAMING_CONTENT_SEPARATOR
                    )
                ).toString()

                openAIAPIClientService
                    .openStructuredArrayStream(
                        prompt = prompt,
                        streamedItemType = object : TypeReference<VocabularySuggestion>() {},
                        onComplete = { (payload, emitter) ->
                            gptTokensUsageService.saveTokensUsage(
                                userId = user.id,
                                operationType = GptTokensUsageOperationType.Words.SUGGEST_VOCABULARY,
                                model = "gpt-4.1-mini",
                                inputTokens = payload.inputTokens,
                                outputTokens = payload.outputTokens
                            ).subscribe(
                                { /* success */ },
                                { error -> logger.error("Failed to log token usage for vocabulary suggestions", error) }
                            )

                            emitter.tryEmitNext(
                                jsonObjectMapper.writeValueAsString(payload.finalContent)
                            )
                        }
                    )
            }
    }
}