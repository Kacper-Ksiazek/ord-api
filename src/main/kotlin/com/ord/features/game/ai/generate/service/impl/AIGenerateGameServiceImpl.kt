package com.ord.features.game.ai.generate.service.impl

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.service.WordService
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.variants.crossword.ai.dto.GeneratedCrosswordGame
import com.ord.features.game.ai.generate.dto.GeneratedSentencesWritingGame
import com.ord.features.game.ai.generate.dto.GeneratedWordsTypingGame
import com.ord.features.game.ai.generate.llm_api_responses.AIGeneratedCrosswordData
import com.ord.features.game.ai.generate.llm_api_responses.AIGeneratedWordsTypingData
import com.ord.features.game.ai.generate.service.AIGenerateGameService
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.extensions.getNumberOfWordsForCrossword
import com.ord.features.game.model.ongoing_game.extensions.getNumberOfWordsForWordsTypingGame
import com.ord.features.game.model.ongoing_game.json.CrosswordProperAnswers
import com.ord.features.game.variants.crossword.dto.CrosswordInstruction
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.ord.shared.domain.enums.SortDirection
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.toParamString
import org.springframework.stereotype.Service

@Service
class AIGenerateGameServiceImpl(
    private val wordService: WordService,
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
) : AIGenerateGameService {
    private fun UserEntity.getProficiencyInLanguage(language: LanguageName): LanguageProficiencyEntity {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(this.id, language)
    }

    override fun generateCrosswordGame(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedCrosswordGame {
        val languageProficiency: LanguageProficiencyEntity = user.getProficiencyInLanguage(language)
        val amountOfQuestion: Int = difficulty.getNumberOfWordsForCrossword()

        val words = getWordsForGame(
            user = user,
            language = language,
            difficulty.getNumberOfWordsForCrossword(),
            maximumWordLength = 18
        ).map { it.origin }

        val prompt = Prompt(
            variant = AvailablePrompts.GAMES_GENERATE_CROSSWORD,
            params = mapOf(
                "language" to language.name,
                "difficulty" to difficulty.name,
                "proficiency" to languageProficiency.proficiency.name,
                "words" to words.toParamString(tabulated = true),
                "amountOfQuestions" to amountOfQuestion.toString(),
            )
        ).toString()


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
            instruction = CrosswordInstruction.Companion.construct(
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
        val languageProficiency: LanguageProficiencyEntity = user.getProficiencyInLanguage(language)
        val amountOfQuestion: Int = difficulty.getNumberOfWordsForWordsTypingGame()

        val words = getWordsForGame(
            user = user,
            language = language,
            n = amountOfQuestion
        ).map { it.origin }

        val prompt = Prompt(
            variant = AvailablePrompts.GAMES_GENERATE_WORDS_TYPING,
            params = mapOf(
                "language" to language.name,
                "difficulty" to difficulty.name,
                "proficiency" to languageProficiency.proficiency.name,
                "words" to words.toParamString(tabulated = true),
                "amountOfQuestions" to amountOfQuestion.toString(),
                "generativeContentLanguage" to languageProficiency.generativeContentLanguage.toString(),
            )
        ).toString()

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
                        words.all { parsedResponseBody.keys.contains(it) }
            },

            parseResponseBody = { rawResponseBody ->
                words.associateWith {
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