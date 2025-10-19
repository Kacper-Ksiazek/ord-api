package com.ord.controllers.games

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.repositories.WordRepository
import com.ord.features.game.model.finished_game.FinishedGameDTO
import com.ord.features.game.model.finished_game.FinishedGameMapper
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.OngoingSentencesWritingGameDTO
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.game.repositories.OngoingGameRepository
import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameRequest
import com.ord.features.game.variants.sentences_writing.dto.api_responses.FinishedSentencesWritingGameResponse
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_requests.UnsafeStartGameRequestData
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.repository.UserActivityLogRepository
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.factories.WordFactory
import com.ord.testing_utils.api.clients.games.SentencesWritingGameAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import com.ord.testing_utils.mocks.games.GameMockerBase
import com.ord.testing_utils.mocks.games.sentences_writing.SentencesWritingAnswersMockLoader
import com.ord.testing_utils.mocks.games.sentences_writing.SentencesWritingGameMocker
import com.ord.utils.resource_readers.loadWordsFromResourceFile
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DisplayName("- SentencesWritingGameController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000") // 3 minutes
class TestSentencesWritingGameController @Autowired constructor(
    private val userActivityLogRepository: UserActivityLogRepository,
    private val userSeeder: UserSeeder,
    private val wordMockFactory: WordFactory,
    private val finishedGameMapper: FinishedGameMapper,
    private val wordRepository: WordRepository,
    private val ongoingGameMapper: OngoingGameMapper,
    private val ongoingGameRepository: OngoingGameRepository,
    private val finishedGameRepository: FinishedGameRepository,
    userRepository: UserRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder
) {
    private val sentencesWritingGameAPIClient = SentencesWritingGameAPIClient(webClient)

    private val sentencesWritingGameMocker = SentencesWritingGameMocker(
        apiClient = sentencesWritingGameAPIClient,
        ongoingGameMapper = ongoingGameMapper,
        ongoingGameRepository = ongoingGameRepository,
        wordMockFactory = wordMockFactory,
        wordRepository = wordRepository
    )

    @Nested
    @DisplayName("[POST] /api/v1/games/sentences-writing/start - start a new sentences writing game")
    inner class StartSentencesWritingGame {
        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            lateinit var authenticatedUser: MockedAuthenticatedUser
            lateinit var gameSavedInDb: OngoingSentencesWritingGameDTO
            lateinit var gameSentToUser: StartedSentencesWritingGameResponse

            @BeforeAll
            fun beforeAll() {
                authenticatedUser = mockAuthenticatedUser()
                with(sentencesWritingGameMocker.mockThroughApiFlow(authenticatedUser)) {
                    gameSavedInDb = first
                    gameSentToUser = second
                }
            }

            @Test
            fun `Game is properly saved in the DB`() {
                gameSavedInDb.id shouldBe gameSentToUser.gameId

                gameSavedInDb.type shouldBe GameType.SENTENCES_WRITING
                gameSavedInDb.language shouldBe GameMockerBase.Companion.DefaultParams.language
                gameSavedInDb.difficulty shouldBe GameMockerBase.Companion.DefaultParams.difficulty

                gameSavedInDb.userId shouldBe authenticatedUser.userInfo.id
            }

            @Test
            fun `Proper answers (instructions) are correctly saved in the DB`() {
                gameSentToUser.instruction.size shouldBe gameSavedInDb.properAnswers.size

                gameSentToUser.instruction.forEach { instruction ->
                    gameSavedInDb.properAnswers.any { it.id == instruction.id } shouldBe true
                }
            }

            @Test
            fun `All words should be unique`() {
                gameSavedInDb.properAnswers.map { it.word }.distinct().size shouldBe gameSavedInDb.properAnswers.size
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - Anonymous user cannot start a sentences writing game`() {
                val response = sentencesWritingGameAPIClient.startGame(
                    body = StartGameRequest(language = LanguageName.ENGLISH, difficulty = GameDifficulty.HARD),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - User with no words assigned cannot start a sentences writing game`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = sentencesWritingGameAPIClient.startGame(
                    body = StartGameRequest(language = LanguageName.ENGLISH, difficulty = GameDifficulty.HARD),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - User with insufficient number of words assigned cannot start a sentences writing game`() {
                val requiredNumberOfWords = 6
                val authenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository,
                    numberOfWordsToLoad = requiredNumberOfWords - 1
                )

                val response = sentencesWritingGameAPIClient.startGame(
                    body = StartGameRequest(language = LanguageName.ENGLISH, difficulty = GameDifficulty.HARD),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - User with no proficiency in the language cannot start a sentences writing game`() {
                val unknownForUserLanguage = LanguageName.ITALIAN
                val authenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val response = sentencesWritingGameAPIClient.startGame(
                    body = StartGameRequest(language = unknownForUserLanguage, difficulty = GameDifficulty.HARD),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Cannot start a game without providing difficulty`() {
                val authenticatedUser = mockAuthenticatedUser()
                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val response = sentencesWritingGameAPIClient.startGame(
                    body = UnsafeStartGameRequestData(language = LanguageName.ENGLISH, difficulty = null),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Cannot start a game without providing language`() {
                val authenticatedUser = mockAuthenticatedUser()

                loadWordsFromResourceFile(
                    userId = authenticatedUser.userInfo.id,
                    wordsRepository = wordRepository
                )

                val response = sentencesWritingGameAPIClient.startGame(
                    body = UnsafeStartGameRequestData(language = null, difficulty = GameDifficulty.HARD),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/games/sentences-writing/finish - finish an ongoing sentences writing game")
    inner class FinishSentencesWritingGame {
        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            var finishedGameInDb: FinishedGameDTO? = null
            lateinit var completedOngoingGameId: UUID
            lateinit var authenticatedUser: MockedAuthenticatedUser
            lateinit var finishedGameResponse: FinishedSentencesWritingGameResponse

            val gameAnswerLoader = SentencesWritingAnswersMockLoader()

            private fun finishSentencesWritingGame(game: OngoingSentencesWritingGameDTO): FinishedSentencesWritingGameResponse {
                val response = sentencesWritingGameAPIClient.finishGame(
                    body = FinishSentencesWritingGameRequest(
                        gameId = game.id,
                        duration = "00:01:00",
                        answers = gameAnswerLoader.mockAnswers(game.difficulty)
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                finishedGameInDb = finishedGameRepository
                    .findAllByUserId(authenticatedUser.userInfo.id)
                    .collectList()
                    .block()!!
                    .firstOrNull()
                    ?.let { finishedGameMapper.toDTOOrNull(it) }

                return response.body!!
            }

            @BeforeAll
            fun beforeAll() {
                authenticatedUser = mockAuthenticatedUser()

                val ongoingGame: OngoingSentencesWritingGameDTO = sentencesWritingGameMocker
                    .mockFromJsonSource(authenticatedUser.userInfo.id)
                    .first
                    .apply {
                        completedOngoingGameId = this.id
                    }

                finishedGameResponse = finishSentencesWritingGame(ongoingGame)
            }

            @Test
            fun `Game is finished and marked as finished in the DB`() {
                finishedGameInDb shouldNotBe null
            }

            @Test
            fun `Criteria should be evaluated`() {
                finishedGameResponse.reviewedAnswers.forEach {
                    val accuracy = it.score.toDouble() / it.maxScore.toDouble()
                    accuracy shouldBeGreaterThan 0.6

                    it.evaluationCriteria.fitsTopic shouldBe true

                    it.evaluationCriteria.answerLength.score shouldBeGreaterThan 3
                    it.evaluationCriteria.vocabulary.score shouldBeGreaterThan 3
                    it.evaluationCriteria.correctWordUsage.score shouldBeGreaterThan 3
                }
            }

            @Test
            fun `Word points are updated in the DB`() {
                val words = wordRepository.findAllByUserId(authenticatedUser.userInfo.id).collectList().block()!!

                val wordsUsedInGame: List<String> = finishedGameResponse.reviewedAnswers.map { it.word }
                val usedWordEntities: List<WordEntity> =
                    words.filter { it.translatedFrom == finishedGameInDb?.language && it.origin in wordsUsedInGame }

                finishedGameResponse.reviewedAnswers.forEach { reviewedTopic ->
                    val wordEntity = usedWordEntities.firstOrNull { it.origin == reviewedTopic.word }
                    wordEntity shouldNotBe null

                    val accuracy = reviewedTopic.score.toDouble() / reviewedTopic.maxScore.toDouble()
                    val expectedGrade = WordAnswerScore.fromDouble(accuracy)

                    wordEntity?.points shouldBe expectedGrade.dbPoints
                }
            }

            @Test
            fun `Ongoing game record is deleted upon finish`() {
                ongoingGameRepository.findById(completedOngoingGameId).block() shouldBe null
            }

            @Test
            fun `User activity log is created after finishing the game`() {
                val userActivityLogs = userActivityLogRepository
                    .findAllByUserId(authenticatedUser.userInfo.id)
                    .collectList()
                    .block()!!
                    .filter {
                        it.type == UserActivityType.SENTENCES_WRITING_GAME_COMPLETED_FLAWLESSLY ||
                                it.type == UserActivityType.SENTENCES_WRITING_GAME_COMPLETED_WITH_MISTAKES
                    }

                userActivityLogs shouldHaveSize 1
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - Anonymous user cannot finish a sentences writing game`() {
                val response = sentencesWritingGameAPIClient.finishGame(
                    body = FinishSentencesWritingGameRequest(
                        gameId = UUID.randomUUID(),
                        duration = "00:01:00",
                        answers = mapOf(UUID.randomUUID() to "answer")
                    ),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - User cannot finish a game that does not belong to them`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val otherUserId = userSeeder.seedOneEntity().id
                val sentencesWritingSavedInDb = sentencesWritingGameMocker.mockFromJsonSource(
                    userId = otherUserId!!,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val response = sentencesWritingGameAPIClient.finishGame(
                    body = FinishSentencesWritingGameRequest(
                        gameId = sentencesWritingSavedInDb.id,
                        duration = "00:01:00",
                        answers = mapOf(UUID.randomUUID() to "answer")
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `400 - Sentences writing cannot be finished with user answer to single sentence over 1024 characters long`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val sentencesWritingSavedInDb = sentencesWritingGameMocker.mockFromJsonSource(
                    userId = authenticatedUser.userInfo.id,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val response = sentencesWritingGameAPIClient.finishGame(
                    body = FinishSentencesWritingGameRequest(
                        gameId = sentencesWritingSavedInDb.id,
                        duration = "00:01:00",
                        answers = mapOf(UUID.randomUUID() to "x".repeat(1025))
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Duration must be in format hh mm ss`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val sentencesWritingSavedInDb = sentencesWritingGameMocker.mockFromJsonSource(
                    userId = authenticatedUser.userInfo.id,
                    difficulty = GameDifficulty.MEDIUM
                ).first

                val response = sentencesWritingGameAPIClient.finishGame(
                    body = FinishSentencesWritingGameRequest(
                        gameId = sentencesWritingSavedInDb.id,
                        duration = "invalid duration format",
                        answers = mapOf(UUID.randomUUID() to "answer")
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }
}