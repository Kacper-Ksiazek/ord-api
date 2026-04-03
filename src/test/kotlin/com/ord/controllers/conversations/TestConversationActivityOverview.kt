package com.ord.controllers.conversations

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.features.conversation.models.conversation.ConversationEntity
import com.ord.features.conversation.models.conversation.enums.ConversationTone
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.conversation_activity.HeatmapPercentile
import com.ord.features.conversation.repositories.ConversationRepository
import com.ord.testing_utils.api.clients.ConversationAPIClient
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

@DisplayName("- ConversationController: Activity Overview")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000")
class TestConversationActivityOverview @Autowired constructor(
    private val conversationRepository: ConversationRepository,
    private val databaseClient: org.springframework.r2dbc.core.DatabaseClient,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    gptTokensUsageRepository: GptTokensUsageRepository
) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    gptTokensUsageRepository = gptTokensUsageRepository
) {
    private val conversationAPIClient = ConversationAPIClient(webClient)

    @AfterEach
    fun cleanup() {
        conversationRepository.deleteAll().block()
    }

    private fun createConversationAt(userId: UUID, createdAt: Instant): ConversationEntity {
        return conversationRepository.save(
            ConversationEntity(
                topic = "Test topic",
                additionalContext = null,
                language = LanguageName.ENGLISH,
                proficiencyLevel = LanguageProficiencyLevel.B2,
                type = ConversationType.SMALL_TALK,
                aiTone = ConversationTone.FRIENDLY,
                aiInterlocutorName = "Bot",
                aiInterlocutorAvatarId = "AVATAR_ALPHA",
                userId = userId,
                createdAt = createdAt,
            )
        ).block()!!
    }

    private fun insertMessageAt(
        conversationId: UUID,
        createdAt: Instant,
        messageOrder: Int = 1,
        sender: String = "USER",
    ) {
        databaseClient.sql(
            """
            INSERT INTO conversation_messages (id, content, message_order, sender, conversation_id, created_at)
            VALUES (:id, :content, :messageOrder, :sender, :conversationId, :createdAt)
            """
        )
            .bind("id", UUID.randomUUID())
            .bind("content", "Test message")
            .bind("messageOrder", messageOrder)
            .bind("sender", sender)
            .bind("conversationId", conversationId)
            .bind("createdAt", createdAt)
            .fetch()
            .rowsUpdated()
            .block()
    }

    private fun instantOf(date: LocalDate): Instant =
        date.atTime(12, 0).toInstant(ZoneOffset.UTC)

    @Nested
    @DisplayName("[GET] /api/v1/conversations/overview")
    inner class GetActivityOverview {

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should reject unauthenticated request`() {
                val response = conversationAPIClient.getActivityOverview(user = null)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return overview with correct structure for empty data`() {
                val user = mockAuthenticatedUser()
                val response = conversationAPIClient.getActivityOverview(user = user)

                response.status shouldBe HttpStatus.OK
                val body = response.body!!

                body.periodLabel shouldBe "Last 90 days"
                body.messagesTotal shouldBe 0L
                body.conversationsTotal shouldBe 0L
                body.heatmap shouldHaveSize 90
                body.heatmap.forEach { day ->
                    day.count shouldBe 0L
                    day.percentile shouldBe HeatmapPercentile.P0
                }
            }

            @Test
            fun `200 - should count conversations and only USER messages`() {
                val user = mockAuthenticatedUser()
                val today = LocalDate.now(ZoneOffset.UTC)

                val conv1 = createConversationAt(user.userInfo.id, instantOf(today))
                val conv2 = createConversationAt(user.userInfo.id, instantOf(today.minusDays(1)))

                // USER messages — should be counted
                insertMessageAt(conv1.id!!, instantOf(today), messageOrder = 1, sender = "USER")
                insertMessageAt(conv1.id!!, instantOf(today), messageOrder = 3, sender = "USER")
                insertMessageAt(conv2.id!!, instantOf(today.minusDays(1)), messageOrder = 1, sender = "USER")

                // AI messages — should NOT be counted
                insertMessageAt(conv1.id!!, instantOf(today), messageOrder = 2, sender = "AI")
                insertMessageAt(conv2.id!!, instantOf(today.minusDays(1)), messageOrder = 2, sender = "AI")

                val response = conversationAPIClient.getActivityOverview(user = user)

                response.status shouldBe HttpStatus.OK
                val body = response.body!!

                body.conversationsTotal shouldBe 2L
                body.messagesTotal shouldBe 3L
            }

            @Test
            fun `200 - should not include data from other users`() {
                val userA = mockAuthenticatedUser()
                val userB = mockAuthenticatedUser()
                val today = LocalDate.now(ZoneOffset.UTC)

                val convA = createConversationAt(userA.userInfo.id, instantOf(today))
                insertMessageAt(convA.id!!, instantOf(today))

                val convB = createConversationAt(userB.userInfo.id, instantOf(today))
                insertMessageAt(convB.id!!, instantOf(today))

                val responseA = conversationAPIClient.getActivityOverview(user = userA)
                responseA.body!!.conversationsTotal shouldBe 1L
                responseA.body!!.messagesTotal shouldBe 1L

                val responseB = conversationAPIClient.getActivityOverview(user = userB)
                responseB.body!!.conversationsTotal shouldBe 1L
                responseB.body!!.messagesTotal shouldBe 1L
            }

            @Test
            fun `200 - should not count activity older than 90 days`() {
                val user = mockAuthenticatedUser()
                val today = LocalDate.now(ZoneOffset.UTC)
                val withinRange = today.minusDays(30)
                val outsideRange = today.minusDays(91)

                val recentConv = createConversationAt(user.userInfo.id, instantOf(withinRange))
                insertMessageAt(recentConv.id!!, instantOf(withinRange))

                val oldConv = createConversationAt(user.userInfo.id, instantOf(outsideRange))
                insertMessageAt(oldConv.id!!, instantOf(outsideRange))

                val response = conversationAPIClient.getActivityOverview(user = user)
                val body = response.body!!

                body.conversationsTotal shouldBe 1L
                body.messagesTotal shouldBe 1L
            }

            @Test
            fun `200 - heatmap entries are ordered ascending and have correct date range`() {
                val user = mockAuthenticatedUser()
                val response = conversationAPIClient.getActivityOverview(user = user)
                val heatmap = response.body!!.heatmap

                val today = LocalDate.now(ZoneOffset.UTC)
                val startDate = today.minusDays(89)

                heatmap.first().date shouldBe startDate.toString()
                heatmap.last().date shouldBe today.toString()

                // Verify ordering
                val dates = heatmap.map { it.date }
                dates shouldBe dates.sorted()
            }

            @Test
            fun `200 - weekly series contains valid weekRange format`() {
                val user = mockAuthenticatedUser()
                val response = conversationAPIClient.getActivityOverview(user = user)
                val body = response.body!!

                body.messagesByWeek.forEach { point ->
                    point.weekRange shouldNotBe null
                    // weekRange should match pattern like "17-23" or "27-3"
                    point.weekRange.split("-") shouldHaveSize 2
                }

                body.conversationsByWeek.forEach { point ->
                    point.weekRange shouldNotBe null
                    point.weekRange.split("-") shouldHaveSize 2
                }
            }

            @Test
            fun `200 - active days have non-P0 percentile when there is contrast`() {
                val user = mockAuthenticatedUser()
                val today = LocalDate.now(ZoneOffset.UTC)

                // Create a burst of messages on one day to create percentile contrast
                val conv = createConversationAt(user.userInfo.id, instantOf(today))
                for (i in 1..20) {
                    insertMessageAt(conv.id!!, instantOf(today), messageOrder = i)
                }

                val response = conversationAPIClient.getActivityOverview(user = user)
                val body = response.body!!

                val todayEntry = body.heatmap.last()
                todayEntry.count shouldBe 20L
                // With 89 zero days and 1 day with 20, that day should be high percentile
                todayEntry.percentile shouldNotBe HeatmapPercentile.P0
            }

            @Test
            fun `200 - weekly totals are consistent with overall total`() {
                val user = mockAuthenticatedUser()
                val today = LocalDate.now(ZoneOffset.UTC)

                val conv = createConversationAt(user.userInfo.id, instantOf(today))
                insertMessageAt(conv.id!!, instantOf(today), messageOrder = 1)
                insertMessageAt(conv.id!!, instantOf(today), messageOrder = 2)

                val response = conversationAPIClient.getActivityOverview(user = user)
                val body = response.body!!

                val weeklyMessageSum = body.messagesByWeek.sumOf { it.count }
                weeklyMessageSum shouldBe body.messagesTotal

                val weeklyConvSum = body.conversationsByWeek.sumOf { it.count }
                weeklyConvSum shouldBe body.conversationsTotal
            }
        }
    }
}
