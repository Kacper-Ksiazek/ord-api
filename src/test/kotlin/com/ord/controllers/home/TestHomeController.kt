package com.ord.controllers.home

import com.ord.config.properties.SessionProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.repositories.WordRepository
import com.ord.features.conversation.models.conversation.ConversationEntity
import com.ord.features.conversation.models.conversation.enums.ConversationTone
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.conversation_message.ConversationMessageEntity
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.repositories.ConversationMessageRepository
import com.ord.features.conversation.repositories.ConversationRepository
import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameGrade
import com.ord.features.game.model.ongoing_game.enums.GameResult
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.home.model.HomeActivityDay
import com.ord.testing_utils.api.clients.HomeAPIClient
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@DisplayName("- HomeController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestHomeController @Autowired constructor(
    private val wordRepository: WordRepository,
    private val conversationRepository: ConversationRepository,
    private val conversationMessageRepository: ConversationMessageRepository,
    private val finishedGameRepository: FinishedGameRepository,
    webClient: WebTestClient,
    sessionProperties: SessionProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    aiProviderUsageRepository: AiProviderUsageRepository,
) : ControllerTestBase(
    webClient = webClient,
    sessionProperties = sessionProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    aiProviderUsageRepository = aiProviderUsageRepository,
) {
    private val homeAPIClient = HomeAPIClient(webClient)

    @AfterEach
    fun cleanup() {
        conversationMessageRepository.deleteAll().block()
        conversationRepository.deleteAll().block()
        finishedGameRepository.deleteAll().block()
        wordRepository.deleteAll().block()
    }

    @Nested
    @DisplayName("[GET] /api/v1/home")
    inner class GetHome {

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should reject unauthenticated request`() {
                val response = homeAPIClient.getHome(user = null)

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return zeros and an empty year when no learning language is selected`() {
                val user = mockAuthenticatedUserWithUninitializedAccount()
                val year = LocalDate.now(ZoneOffset.UTC).year

                val response = homeAPIClient.getHome(user = user)

                response.status shouldBe HttpStatus.OK
                val body = response.body!!
                body.words.total shouldBe 0
                body.words.addedLast30Days shouldBe 0
                body.words.byType shouldBe emptyMap()
                body.conversations.total shouldBe 0
                body.conversations.messagesTotal shouldBe 0
                body.conversations.createdLast30Days shouldBe 0
                body.conversations.messagesLast30Days shouldBe 0
                body.games.comingSoon shouldBe true
                body.games.total shouldBe 0
                body.games.last30Days shouldBe 0
                body.activity.year shouldBe year
                body.activity.days shouldBe emptyList()
            }

            @Test
            fun `200 - should count the selected language across words, messages, and finished games`() {
                val user = mockAuthenticatedUser()
                val otherUser = mockAuthenticatedUser()
                val userId = user.userInfo.id
                val now = Instant.now()
                val older = now.minus(40, ChronoUnit.DAYS)
                val today = utcDate(now)
                val olderDate = utcDate(older)
                val year = LocalDate.now(ZoneOffset.UTC).year

                wordRepository.saveAll(
                    listOf(
                        word(userId = userId, source = "apple", type = WordType.NOUN, createdAt = now),
                        word(userId = userId, source = "pear", type = WordType.NOUN, createdAt = now),
                        word(userId = userId, source = "run", type = WordType.VERB, createdAt = older),
                        word(
                            userId = userId,
                            source = "dom",
                            type = WordType.NOUN,
                            language = LanguageName.POLISH,
                            createdAt = now,
                        ),
                        word(userId = otherUser.userInfo.id, source = "other", type = WordType.NOUN, createdAt = now),
                    ),
                ).collectList().block()

                val recentConversation = conversationRepository.save(
                    conversation(userId = userId, topic = "recent", createdAt = now),
                ).block()!!
                val olderConversation = conversationRepository.save(
                    conversation(userId = userId, topic = "older", createdAt = older),
                ).block()!!
                conversationRepository.save(
                    conversation(
                        userId = userId,
                        topic = "other-language",
                        language = LanguageName.POLISH,
                        createdAt = now,
                    ),
                ).block()

                conversationMessageRepository.saveAll(
                    listOf(
                        message(
                            conversationId = recentConversation.id!!,
                            sender = ConversationMessageSender.USER,
                            order = 0,
                            createdAt = now,
                        ),
                        message(
                            conversationId = recentConversation.id!!,
                            sender = ConversationMessageSender.AI,
                            order = 1,
                            createdAt = now,
                        ),
                        message(
                            conversationId = olderConversation.id!!,
                            sender = ConversationMessageSender.USER,
                            order = 0,
                            createdAt = older,
                        ),
                    ),
                ).collectList().block()

                finishedGameRepository.saveAll(
                    listOf(
                        finishedGame(userId = userId, result = GameResult.COMPLETED, createdAt = now),
                        finishedGame(userId = userId, result = GameResult.CANCELLED, createdAt = now),
                        finishedGame(
                            userId = userId,
                            result = GameResult.COMPLETED,
                            language = LanguageName.POLISH,
                            createdAt = now,
                        ),
                    ),
                ).collectList().block()

                val response = homeAPIClient.getHome(user = user)

                response.status shouldBe HttpStatus.OK
                val body = response.body!!
                body.words.total shouldBe 3
                body.words.addedLast30Days shouldBe 2
                body.words.byType.shouldContainExactly(
                    mapOf(
                        WordType.NOUN to 2L,
                        WordType.VERB to 1L,
                    ),
                )
                body.conversations.total shouldBe 2
                body.conversations.messagesTotal shouldBe 3
                body.conversations.createdLast30Days shouldBe 1
                body.conversations.messagesLast30Days shouldBe 2
                body.games.comingSoon shouldBe true
                body.games.total shouldBe 2
                body.games.last30Days shouldBe 2
                body.activity.year shouldBe year

                val expectedDays = buildList {
                    if (olderDate.year == year) {
                        add(HomeActivityDay(date = olderDate.toString(), count = 2))
                    }
                    add(HomeActivityDay(date = today.toString(), count = 6))
                }
                body.activity.days shouldBe expectedDays
            }
        }
    }

    private fun utcDate(instant: Instant): LocalDate = instant.atZone(ZoneOffset.UTC).toLocalDate()

    private fun word(
        userId: java.util.UUID,
        source: String,
        type: WordType,
        createdAt: Instant,
        language: LanguageName = LanguageName.ENGLISH,
    ) = WordEntity(
        type = type,
        sourceWord = source,
        translation = source,
        language = language,
        userId = userId,
        createdAt = createdAt,
        updatedAt = createdAt,
    )

    private fun conversation(
        userId: java.util.UUID,
        topic: String,
        createdAt: Instant,
        language: LanguageName = LanguageName.ENGLISH,
    ) = ConversationEntity(
        topic = topic,
        language = language,
        proficiencyLevel = LanguageProficiencyLevel.C1,
        type = ConversationType.SMALL_TALK,
        aiTone = ConversationTone.FRIENDLY,
        aiInterlocutorName = "Sarah",
        aiInterlocutorAvatarId = "avatar-1",
        userId = userId,
        createdAt = createdAt,
        updatedAt = createdAt,
    )

    private fun message(
        conversationId: java.util.UUID,
        sender: ConversationMessageSender,
        order: Int,
        createdAt: Instant,
    ) = ConversationMessageEntity(
        content = "hello",
        messageOrder = order,
        sender = sender,
        conversationId = conversationId,
        createdAt = createdAt,
    )

    private fun finishedGame(
        userId: java.util.UUID,
        result: GameResult,
        createdAt: Instant,
        language: LanguageName = LanguageName.ENGLISH,
    ) = FinishedGameEntity(
        score = if (result == GameResult.COMPLETED) 10 else 0,
        accuracy = if (result == GameResult.COMPLETED) 1f else 0f,
        duration = "00:00:30",
        language = language,
        type = GameType.CROSSWORD,
        result = result,
        difficulty = GameDifficulty.EASY,
        grade = if (result == GameResult.COMPLETED) GameGrade.S else GameGrade.NA,
        userId = userId,
        createdAt = createdAt,
    )
}
