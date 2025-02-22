package com.backend.ord.services.impl.ai

import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.openai.OpenAIRequest
import com.backend.ord.api.requests.openai.OpenAIRequestFactory
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.responses.openai.embedded.OpenAIResponse
import com.backend.ord.api.responses.words.WordListItem
import com.backend.ord.config.RestClientConfig
import com.backend.ord.domain.persistence.embedded.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.game.getNumberOfWordsForCrossword
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.prompts.Prompts
import com.backend.ord.services.LanguageProficiencyService
import com.backend.ord.services.WordService
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.gpt_tokens_usage.GameTokensUsageService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service

@Service
class AIGameServiceImpl(
    private val restClientConfig: RestClientConfig,
    private val openAIRequestFactory: OpenAIRequestFactory,

    // Services:
    private val wordService: WordService,
    private val gameTokensUsageService: GameTokensUsageService,
    private val languageProficiencyService: LanguageProficiencyService
) : AIGameService {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    private fun User.getProficiencyInLanguage(language: LanguageName): LanguageProficiencyLevel {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(this.id, language)
            .proficiency
    }

    /**
     * The process of generating a crossword can be demonstrated as a list of steps:
     *
     * 1. Retrieving words saved by the user
     * 2. Based on the selected difficulty, it is randomly selecting an appropriate number of words
     * 3. Making a request to the OpenAI API to generates:
     *    3.1 `questions`- which are clues for the words, such as: For the word "dog" the question could be: "A pet that barks"
     *    3.2 `answer` - which is the final word that the user needs to guess by solving the crossword. It is designed to show a user a new word or phrase
     *    3.3 `answer explanation` - which is the explanation of the answer
     * 4. Logs the token usage for the request
     * 5. Parses the response into the expected `AIGeneratedCrossword` object
     */
    override fun generateCrosswordGame(
        user: User,
        language: LanguageName,
        difficulty: GameDifficulty
    ): AIGameService.Companion.GeneratedCrossWordGame {
        // Get words for the game
        val words = wordService.findManyWords(
            user = user,
            language = language,
            perPage = 500,
            sortBy = GetAllWordsSortOptions.ORIGIN,
            sortDirection = SortDirection.DESC,
            completed = false
            // TODO: Add more filters
        ).data
            .shuffled()
            .take(difficulty.getNumberOfWordsForCrossword())

        val (aiGeneratedCrossword, gameTokensUsageLogs) = makeOpenAIRequestToGenerateCrossword(
            user = user,
            language = language,
            difficulty = difficulty,
            words = words
        )

        return AIGameService.Companion.GeneratedCrossWordGame(
            aiGeneratedCrossword = aiGeneratedCrossword,
            gameTokensUsageLogs = gameTokensUsageLogs,
            wordsUsedIds = words.map { it.id }.toSet(),
            properAnswers = CrosswordProperAnswers(
                finalWord = aiGeneratedCrossword.answer,
                questions = aiGeneratedCrossword.questions.associate { it.id to it.word }
            )
        )
    }

    private fun makeOpenAIRequestToGenerateCrossword(
        user: User,
        language: LanguageName,
        difficulty: GameDifficulty,
        words: List<WordListItem>
    ): Pair<AIGeneratedCrossword, Set<GameTokensUsage>> {
        val amountOfQuestions = difficulty.getNumberOfWordsForCrossword()

        val openAIRequest: OpenAIRequest = openAIRequestFactory.createRequest(
            prompt = Prompts.generateCrosswordQuestionsPrompt(
                wordsToUse = words.map { it.origin },
                language = language,
                difficulty = difficulty,
                amountOfQuestions = amountOfQuestions,
                languageProficiency = user.getProficiencyInLanguage(language),
            )
        )

        var response: OpenAIResponse
        var parsedResponseBody: AIGeneratedCrossword?
        val gameTokensUsageLogs: MutableSet<GameTokensUsage> = mutableSetOf()

        var openAIAPIRequestAttempt: Int = 0;

        do {
            openAIRequestFactory.trackOpenAIAPIRequestAttempt(openAIAPIRequestAttempt++)

            // Send the request to the OpenAI APIdd
            response = restClientConfig.makeOpenAIPostRequest(openAIRequest).also {
                // Log the token usage for the request
                gameTokensUsageLogs.add(
                    gameTokensUsageService.save(
                        user = user,

                        leadingLanguage = language,
                        gameDifficulty = difficulty,
                        instructionLanguage = language,

                        gameType = GameType.CROSSWORD,
                        consumptionType = GamesGPTTokensConsumptionType.GENERATE,

                        inputTokens = it.usage.prompt_tokens,
                        outputTokens = it.usage.completion_tokens
                    )
                )
            }

            // Attempt to parse the response into the expected AIGeneratedCrossword object
            parsedResponseBody = try {
                val readValue = jsonObjectMapper.readValue<AIGeneratedCrossword>(response.data)

                readValue.copy(
                    questions = with(readValue.questions) {
                        if (size > amountOfQuestions) {
                            shuffled().take(amountOfQuestions)
                        } else {
                            this
                        }
                    }
                )
            } catch (_: Exception) {
                null
            }

            // Retry if the response doesn't have the expected number of questions or some words are repeated
        } while (
            parsedResponseBody?.questions?.size != amountOfQuestions ||
            parsedResponseBody.questions.map { it.word }.distinct().size != amountOfQuestions
        )

        return Pair(parsedResponseBody, gameTokensUsageLogs)
    }
}