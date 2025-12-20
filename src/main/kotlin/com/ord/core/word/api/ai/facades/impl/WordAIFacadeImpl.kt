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
import com.ord.core.word.api.ai.responses.openai.OpenAIGeneratedWordManual
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
                        // Filter on the parsed object
                        val wordLowercase = suggestion?.word?.lowercase()
                        !excludedWordsSet.contains(wordLowercase) && !existingWordsSet.contains(wordLowercase)
                    }
                    .map { suggestion ->
                        // Serialize back to JSON string for the response
                        jsonObjectMapper.writeValueAsString(suggestion)
                    }
            }
    }
}