package com.ord.features.game.variants.shared.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.service.WordService
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.extensions.getNumberOfQuestions
import com.ord.features.game.variants.shared.ai.helpers.GameContext
import com.ord.shared.domain.enums.SortDirection
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

abstract class AIGenerateGameServiceBase<
        TGeneratedGame,
        TAIResponse : Any
        >(
    private val gameType: GameType,
    private val prompt: AvailablePrompts,
    private val aiResponseTypeReference: TypeReference<TAIResponse>,
    private val promptExtraParams: Map<String, String> = emptyMap()
) : AIGameServiceBase() {
    @Autowired
    protected lateinit var wordService: WordService

    @Autowired
    protected lateinit var languageProficiencyService: LanguageProficiencyService

    abstract fun parseAIResponse(
        responseBody: TAIResponse,
        context: GameContext
    ): TAIResponse

    abstract fun validateAIResponse(
        parsedResponseBody: TAIResponse?,
        context: GameContext
    ): Boolean

    abstract fun refineAIResponse(
        aiResponse: TAIResponse,
        context: GameContext
    ): TGeneratedGame

    fun generate(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty,
    ): Mono<TGeneratedGame> {
        return languageProficiencyService.findUserProficiencyInLanguageOrThrow(user.id, language)
            .flatMap { languageProficiency ->
                val amountOfQuestion: Int = difficulty.getNumberOfQuestions(gameType)
                val userLanguageProficiency = languageProficiency.level
                val generativeContentLanguage = languageProficiency.generativeContentLanguage

                getWordsForGame(
                    user = user,
                    language = language,
                    n = amountOfQuestion
                )
                .map { words ->
                    val context = GameContext(
                        language = language,
                        difficulty = difficulty,
                        amountOfQuestion = amountOfQuestion,
                        userLanguageProficiency = userLanguageProficiency,
                        generativeContentLanguage = generativeContentLanguage,
                        words = words
                    )

                    val prompt = Prompt(
                        variant = this.prompt,
                        params = mapOf(
                            "language" to language.name,
                            "difficulty" to difficulty.name,
                            "proficiency" to userLanguageProficiency.name,
                            "words" to words.toParamString(tabulated = true),
                            "amountOfQuestions" to amountOfQuestion.toString(),
                            "generativeContentLanguage" to generativeContentLanguage.toString(),
                        ) + promptExtraParams
                    )

                    Pair(context, prompt)
                }
            }
            .flatMap { (context, prompt) ->
                makeGameAIRequest(
                    prompt = prompt.toString(),
                    aiResponseTypeReference = aiResponseTypeReference,
                    validateResponseBody = { validateAIResponse(it, context) },
                    parseResponseBody = { parseAIResponse(it, context) }
                )
                .map { aiResponse ->
                    refineAIResponse(aiResponse, context)
                }
            }
    }

    protected fun getWordsForGame(
        user: UserEntity,
        language: LanguageName,
        n: Int,
        maximumWordLength: Int? = null,
    ): Mono<List<String>> {
        // TODO: Generate a list of words using AI - plans for the future far far away

        return wordService.findManyWords(
            user = user,
            language = language,
            perPage = 500,
            sortBy = GetAllWordsSortOptions.ORIGIN,
            sortDirection = SortDirection.DESC,
            completed = false
            // TODO: Add more filters
        ).flatMap { paginatedWords ->
            val words = paginatedWords.data
            
            if (words.size < n) {
                Mono.error(BadRequestException("Not enough words to generate a game"))
            } else {
                val shuffledWords = words.shuffled()
                
                val selectedWords = if (maximumWordLength != null) {
                    shuffledWords
                        .take(2 * n)
                        .filter { it.origin.length <= maximumWordLength }
                        .take(n)
                } else {
                    shuffledWords.take(n)
                }.map { it.origin }
                
                Mono.just(selectedWords)
            }
        }
    }
}