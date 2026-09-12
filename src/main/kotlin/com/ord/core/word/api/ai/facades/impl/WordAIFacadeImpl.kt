package com.ord.core.word.api.ai.facades.impl
import com.ord.shared.utils.OrdJsonMapper

import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper
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
import com.ord.core.word.api.ai.WordFillGapsPromptFormatter
import com.ord.core.word.api.ai.requests.dto.WordFillGapsItem
import com.ord.core.word.api.ai.requests.dto.WordFillGapsRequest
import com.ord.core.word.api.ai.responses.dto.AIGeneratedWordManual
import com.ord.core.word.api.ai.responses.dto.VocabularySuggestion
import com.ord.core.word.api.ai.responses.dto.WordFillGapsResponse
import com.ord.core.word.api.ai.responses.dto.WordFillGapsResultItem
import com.ord.core.word.api.ai.responses.openai.OpenAIWordFillGapsBatch
import com.ord.core.word.api.ai.responses.openai.OpenAIGeneratedWordManual
import com.ord.core.word.models.word_details.enums.WordCollocationFrequency
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word_details.enums.WordGender
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.services.WordService
import com.ord.exceptions.REST.BadRequestException
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
    private val gptTokensUsageService: GptTokensUsageService,
) : WordAIFacade {
    private val logger = LoggerFactory.getLogger(WordAIFacadeImpl::class.java)
    private val jsonObjectMapper: JsonMapper = OrdJsonMapper.instance

    override fun generateWordManual(
        body: GenerateWordManualRequest,
        user: UserDTO,
    ): Mono<AIGeneratedWordManual> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMap { userProficiencyInRequestedLanguage ->
                val translateTo: LanguageName =
                    body.targetLanguage ?: userProficiencyInRequestedLanguage.translateTo
                val proficiencyLevel: LanguageProficiencyLevel =
                    body.proficiencyLevel ?: userProficiencyInRequestedLanguage.level

                val prompt = Prompt(
                    variant = AvailablePrompts.WORDS_GENERATE_MANUAL,
                    params = mapOf(
                        "word" to body.word,
                        "wordLanguage" to body.language.toString(),
                        "desiredLanguage" to translateTo.toString(),
                        "proficiency" to proficiencyLevel.toString(),
                        "generativeContentLanguage" to userProficiencyInRequestedLanguage.generativeContentLanguage.toString(),

                        "wordTypes" to WordType::class.joinEnumValues(separator = " | "),
                        "wordExtraMarks" to WordExtraMark::class.joinEnumValues(separator = " | "),
                        "wordCollocationFrequency" to WordCollocationFrequency::class.joinEnumValues(separator = " | "),
                        "wordGenders" to WordGender::class.joinEnumValues(separator = " | ")
                    )
                )

                openAIAPIClientService
                    .makeRequest(
                        aiResponseType = object : TypeReference<OpenAIGeneratedWordManual>() {},
                        prompt = prompt,
                        userId = user.id,
                        gptTokensUsageLogKey = GptTokensUsageOperationType.Words.GENERATE_MANUAL,
                    )
                    .map {
                        it.toDomain(body.word)
                    }
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
            .flatMapMany { userProficiencyInRequestedLanguage ->
                wordService.getWordsForPromptGeneration(
                    userId = user.id,
                    language = body.language,
                    amountOfLatestWord = 1000,
                    amountOfProblematicWord = 0,
                ).flatMapMany { allExistingWords ->
                    val translateTo = userProficiencyInRequestedLanguage.translateTo
                    val proficiencyLevel = userProficiencyInRequestedLanguage.level
                    val wordCount = 10
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

                // Create a set of excluded words in lowercase for case-insensitive filtering
                val excludedWordsSet = (body.excludedWords ?: emptyList())
                    .map { it.lowercase() }
                    .toSet()

                // Create a set of existing words in lowercase for case-insensitive filtering
                val existingWordsSet = allExistingWords
                    .map { it.lowercase() }
                    .toSet()

                openAIAPIClientService
                    .openStructuredArrayStream(
                        prompt = prompt,
                        streamedItemType = object : TypeReference<VocabularySuggestion>() {},
                        userId = user.id,
                        gptTokensUsageLogKey = GptTokensUsageOperationType.Words.SUGGEST_VOCABULARY
                    )
                    .mapNotNull { jsonString ->
                        // Parse JSON once and handle parsing errors
                        try {
                            jsonObjectMapper.readValue(jsonString, VocabularySuggestion::class.java)
                        } catch (e: Exception) {
                            logger.warn("Failed to parse vocabulary suggestion: $jsonString", e)
                            null // Filter out invalid JSON
                        }
                    }
                    .filter { suggestion ->
                        // Filter on the parsed object - skip if word is null/empty
                        val wordLowercase = suggestion.word.lowercase()
                        if (wordLowercase.isBlank()) return@filter false
                        !excludedWordsSet.contains(wordLowercase) && !existingWordsSet.contains(wordLowercase)
                    }
                    .map { suggestion ->
                        jsonObjectMapper.writeValueAsString(suggestion)
                    }
                }
            }
    }

    override fun fillGaps(body: WordFillGapsRequest, user: UserDTO): Mono<com.ord.core.word.api.ai.responses.dto.WordFillGapsResponse> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMap { userProficiencyInRequestedLanguage ->
                val wordsList = WordFillGapsPromptFormatter.formatItemsForPrompt(body.items)

                val prompt = Prompt(
                    variant = AvailablePrompts.QAW_FILL_GAPS,
                    params = mapOf(
                        "words" to wordsList,
                        "wordCount" to body.items.size.toString(),
                        "wordLanguage" to body.language.toString(),
                        "desiredLanguage" to userProficiencyInRequestedLanguage.translateTo.toString(),
                        "proficiency" to userProficiencyInRequestedLanguage.level.toString(),
                        "generativeContentLanguage" to userProficiencyInRequestedLanguage.generativeContentLanguage.toString(),
                        "wordTypes" to WordType::class.joinEnumValues(separator = " | "),
                        "wordExtraMarks" to WordExtraMark::class.joinEnumValues(separator = " | "),
                    ),
                )

                val expectedItemCount = body.items.size

                openAIAPIClientService
                    .makeRequest(
                        aiResponseType = object : TypeReference<OpenAIWordFillGapsBatch>() {},
                        prompt = prompt,
                        userId = user.id,
                        gptTokensUsageLogKey = GptTokensUsageOperationType.Words.FILL_GAPS,
                        validateResponseBody = { batch ->
                            if (batch == null || batch.items.size != expectedItemCount) {
                                return@makeRequest false
                            }
                            try {
                                batch.toDomain()
                                true
                            } catch (_: IllegalArgumentException) {
                                false
                            }
                        },
                    )
                    .map { batch -> mergeKnownFillGapsFields(body.items, batch.toDomain()) }
            }
    }

    private fun mergeKnownFillGapsFields(
        requestItems: List<WordFillGapsItem>,
        response: WordFillGapsResponse,
    ): WordFillGapsResponse {
        val mergedItems = response.items.mapIndexed { index, resultItem ->
            val requestItem = requestItems.getOrNull(index)
            if (requestItem == null || resultItem.error != null) {
                resultItem
            } else {
                resultItem.mergeKnownFieldsFrom(requestItem)
            }
        }

        return WordFillGapsResponse(items = mergedItems)
    }

    private fun WordFillGapsResultItem.mergeKnownFieldsFrom(request: WordFillGapsItem): WordFillGapsResultItem =
        copy(
            translation = request.translation?.trim()?.takeIf { it.isNotEmpty() } ?: translation,
            definition = request.definition?.trim()?.takeIf { it.isNotEmpty() } ?: definition,
            type = request.type ?: type,
            extraMark = request.extraMark ?: extraMark,
        )
}