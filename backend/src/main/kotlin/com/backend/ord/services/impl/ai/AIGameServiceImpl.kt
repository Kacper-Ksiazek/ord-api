package com.backend.ord.services.impl.ai

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.words.WordListItem
import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.persistence.entities.LanguageProficiency
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.game.getNumberOfWordsForCrossword
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.prompts.Prompts
import com.backend.ord.services.LanguageProficiencyService
import com.backend.ord.services.WordService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.ai.OpenAIAPIClientService
import com.backend.ord.services.ai.dto.GeneratedCrosswordGame
import com.backend.ord.services.ai.dto.GeneratedWordsTypingGame
import com.backend.ord.services.ai.dto.ai_responses.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.ai_responses.AIGeneratedWordsTyping
import org.springframework.stereotype.Service

@Service
class AIGameServiceImpl(
    // Services:
    private val wordService: WordService,
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
) : AIGameService {
    private fun User.getProficiencyInLanguage(language: LanguageName): LanguageProficiency {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(this.id, language)
    }

    override fun generateCrosswordGame(
        user: User,
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
                difficulty.getNumberOfWordsForCrossword()
            ).map { it.origin },
        )

        val amountOfQuestion: Int = difficulty.getNumberOfWordsForCrossword()

        val aiGeneratedCrossword = openAIAPIClientService.makeGameRequest<AIGeneratedCrossword>(
            clazz = AIGeneratedCrossword::class.java,

            user = user,
            prompt = prompt,
            difficulty = difficulty,
            leadingLanguage = language,
            instructionLanguage = languageProficiency.generativeContentLanguage,

            gameType = GameType.CROSSWORD,
            consumptionType = GamesGPTTokensConsumptionType.GENERATE,

            retryRequestCondition = { parsedResponseBody ->
                parsedResponseBody?.questions?.size != amountOfQuestion ||
                        parsedResponseBody.questions.map { it.word }.distinct().size != amountOfQuestion
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
        user: User,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedWordsTypingGame {
        val languageProficiency: LanguageProficiency = user.getProficiencyInLanguage(language)

        val wordsToUse = getWordsForGame(
            user = user,
            language = language,
            difficulty.getNumberOfWordsForCrossword()
        ).map { it.origin }

        val prompt: String = Prompts.Games.generateWordsTypingGamePrompt(
            language = language,
            difficulty = difficulty,
            languageProficiency = languageProficiency,
            wordsToUse = wordsToUse
        )

        val amountOfQuestion: Int = difficulty.getNumberOfWordsForCrossword()

        val aiGeneratedWordsTypingGame = openAIAPIClientService.makeGameRequest<AIGeneratedWordsTyping>(
            clazz = AIGeneratedWordsTyping::class.java,

            user = user,
            prompt = prompt,
            difficulty = difficulty,
            leadingLanguage = language,
            instructionLanguage = languageProficiency.generativeContentLanguage,

            gameType = GameType.CROSSWORD,
            consumptionType = GamesGPTTokensConsumptionType.GENERATE,

            retryRequestCondition = { parsedResponseBody ->
                parsedResponseBody?.values?.size != amountOfQuestion ||
                        parsedResponseBody.keys.distinct().size != amountOfQuestion ||
                        wordsToUse.all { it in parsedResponseBody.keys }
            }
        )

        return GeneratedWordsTypingGame(aiGeneratedWordsTypingGame)

    }


    private fun getWordsForGame(
        user: User,
        language: LanguageName,
        requiredNumberOfWords: Int
    ): List<WordListItem> {
        // TODO: Generate a list of words using AI - plans for the future far far away

        return wordService.findManyWords(
            user = user,
            language = language,
            perPage = 500,
            sortBy = GetAllWordsSortOptions.ORIGIN,
            sortDirection = SortDirection.DESC,
            completed = false
            // TODO: Add more filters
        ).data
            .shuffled()
            .take(requiredNumberOfWords)
            .apply {
                if (size < requiredNumberOfWords) {
                    throw BadRequestException("Not enough words to generate a crossword game")
                }
            }
    }
}