package com.backend.ord.services.impl.ai

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.words.WordListItem
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.persistence.entities.LanguageProficiency
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.game.getNumberOfWordsForCrossword
import com.backend.ord.enums.persistence.game.getNumberOfWordsForWordsTypingGame
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.prompts.Prompts
import com.backend.ord.services.LanguageProficiencyService
import com.backend.ord.services.WordService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.ai.OpenAIAPIClientService
import com.backend.ord.services.ai.dto.ai_responses.games.AIGeneratedCrosswordData
import com.backend.ord.services.ai.dto.ai_responses.games.AIGeneratedWordsTypingData
import com.backend.ord.services.ai.dto.generated_games.GeneratedCrosswordGame
import com.backend.ord.services.ai.dto.generated_games.GeneratedSentencesWritingGame
import com.backend.ord.services.ai.dto.generated_games.GeneratedWordsTypingGame
import org.springframework.stereotype.Service

@Service
class AIGameServiceImpl(
    // Services:
    private val wordService: WordService,
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
) : AIGameService {
    private fun UserEntity.getProficiencyInLanguage(language: LanguageName): LanguageProficiency {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(this.id, language)
    }

    override fun generateCrosswordGame(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedCrosswordGame {
        val languageProficiency: LanguageProficiency = user.getProficiencyInLanguage(language)

        val prompt: String = Prompts.Games.generateCrosswordQuestionsPrompt(
            language = language,
            difficulty = difficulty,
            languageProficiency = languageProficiency,
            wordsToUse = getWordsForGame(
                user = user,
                language = language,
                difficulty.getNumberOfWordsForCrossword(),
                maximumWordLength = 18
            ).map { it.origin },
        )

        val amountOfQuestion: Int = difficulty.getNumberOfWordsForCrossword()

        val aiGeneratedCrossword = openAIAPIClientService.makeGameRequest<AIGeneratedCrosswordData>(
            clazz = AIGeneratedCrosswordData::class.java,

            user = user,
            prompt = prompt,
            difficulty = difficulty,
            language = language,

            gameType = GameType.CROSSWORD,
            consumptionType = GamesGPTTokensConsumptionType.GENERATE,

            validateResponseBody = { parsedResponseBody ->
                parsedResponseBody?.questions?.size == amountOfQuestion &&
                        parsedResponseBody.questions.map { it.word }.distinct().size == amountOfQuestion
            },

            parseResponseBody = { responseBody ->
                responseBody.copy(
                    questions = with(responseBody.questions) {
                        if (size > difficulty.getNumberOfWordsForCrossword()) {
                            shuffled().take(difficulty.getNumberOfWordsForCrossword())
                        } else {
                            this
                        }
                    }
                )
            }
        )

        return GeneratedCrosswordGame(
            instruction = CrosswordInstruction.construct(
                aiGeneratedQuestions = aiGeneratedCrossword,
                difficulty = difficulty
            ),
            properAnswers = CrosswordProperAnswers(
                finalWord = aiGeneratedCrossword.answer,
                questions = aiGeneratedCrossword.questions.associate { it.id to it.word }
            )
        )
    }

    override fun generateWordsTypingGame(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedWordsTypingGame {
        val languageProficiency: LanguageProficiency = user.getProficiencyInLanguage(language)

        val amountOfQuestion: Int = difficulty.getNumberOfWordsForWordsTypingGame()

        val wordsToUse = getWordsForGame(
            user = user,
            language = language,
            n = amountOfQuestion
        ).map { it.origin }

        val prompt: String = Prompts.Games.generateWordsTypingGamePrompt(
            language = language,
            difficulty = difficulty,
            languageProficiency = languageProficiency,
            wordsToUse = wordsToUse
        )


        val aiGeneratedWordsTypingGame = openAIAPIClientService.makeGameRequest<AIGeneratedWordsTypingData>(
            clazz = AIGeneratedWordsTypingData::class.java,

            user = user,
            prompt = prompt,
            difficulty = difficulty,
            language = language,

            gameType = GameType.WORDS_TYPING,
            consumptionType = GamesGPTTokensConsumptionType.GENERATE,

            validateResponseBody = { parsedResponseBody ->
                parsedResponseBody?.values?.size == amountOfQuestion &&
                        parsedResponseBody.keys.distinct().size == amountOfQuestion &&
                        wordsToUse.all { parsedResponseBody.keys.contains(it) }
            },

            parseResponseBody = { rawResponseBody ->
                wordsToUse.associateWith {
                    (rawResponseBody[it] ?: throw BadRequestException("AI response is not valid! $it not found"))
                }
            }
        )

        return GeneratedWordsTypingGame(aiGeneratedWordsTypingGame)

    }

    override fun generateSentencesWritingGame(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedSentencesWritingGame {
        TODO("Not yet implemented")
    }


    private fun getWordsForGame(
        user: UserEntity,
        language: LanguageName,
        n: Int,
        maximumWordLength: Int? = null,
    ): List<WordListItem> {
        // TODO: Generate a list of words using AI - plans for the future far far away

        val words = wordService.findManyWords(
            user = user,
            language = language,
            perPage = 500,
            sortBy = GetAllWordsSortOptions.ORIGIN,
            sortDirection = SortDirection.DESC,
            completed = false
            // TODO: Add more filters
        ).data
            .apply {
                if (size < n) {
                    throw BadRequestException("Not enough words to generate a game")
                }
            }
            .shuffled()

        return if (maximumWordLength != null) {
            words
                .take(2 * n)
                .filter { it.origin.length <= maximumWordLength }
                .take(n)
        } else {
            words.take(n)
        }
    }
}