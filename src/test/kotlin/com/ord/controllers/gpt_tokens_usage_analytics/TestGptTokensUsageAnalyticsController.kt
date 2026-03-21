package com.ord.controllers.gpt_tokens_usage_analytics

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.models.GptTokensUsageEntity
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.gpt_tokens_usage_analytics.requests.dto.AnalyticsPeriod
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import com.ord.testing_utils.api.clients.GptTokensUsageAnalyticsAPIClient
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
import java.time.temporal.ChronoUnit
import java.util.*

@DisplayName("- GptTokensUsageAnalyticsController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestGptTokensUsageAnalyticsController @Autowired constructor(
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    gptTokensUsageRepository: GptTokensUsageRepository
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    gptTokensUsageRepository = gptTokensUsageRepository
) {
    private val apiClient = GptTokensUsageAnalyticsAPIClient(webClient)

    private fun seedTokensUsage(
        userId: UUID,
        operationType: String,
        inputTokens: Int,
        outputTokens: Int,
        createdAt: Instant = Instant.now()
    ): GptTokensUsageEntity {
        return gptTokensUsageRepository.save(
            GptTokensUsageEntity(
                operationType = operationType,
                model = GptTokensUsageEntity.DEFAULT_MODEL,
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                userId = userId,
                createdAt = createdAt
            )
        ).block()!!
    }

    @Nested
    @DisplayName("[GET] /api/v1/gpt-tokens-usage/ - paginated records")
    inner class GetRecords {

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            @DisplayName("200 - should return paginated records for authenticated user")
            fun shouldReturnPaginatedRecords() {
                val user = mockAuthenticatedUser()

                seedTokensUsage(user.userInfo.id, "CONVERSATION_AI_RESPONSE", 100, 50)
                seedTokensUsage(user.userInfo.id, "WORD_EXPLANATION", 200, 80)
                seedTokensUsage(user.userInfo.id, "QAW_PROCESSING", 150, 60)

                val response = apiClient.getRecords(user = user)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize 3
                response.body!!.pagination.totalResults shouldBe 3L

                val record = response.body!!.data.first()
                record.id shouldNotBe null
                record.operationType shouldNotBe null
                record.model shouldBe GptTokensUsageEntity.DEFAULT_MODEL
                record.inputTokens shouldNotBe null
                record.outputTokens shouldNotBe null
                record.createdAt shouldNotBe null
            }

            @Test
            @DisplayName("200 - should return records ordered by createdAt DESC")
            fun shouldReturnRecordsOrderedByCreatedAtDesc() {
                val user = mockAuthenticatedUser()

                val oldest = Instant.now().minus(3, ChronoUnit.DAYS)
                val middle = Instant.now().minus(2, ChronoUnit.DAYS)
                val newest = Instant.now().minus(1, ChronoUnit.DAYS)

                seedTokensUsage(user.userInfo.id, "OLDEST_OP", 100, 50, oldest)
                seedTokensUsage(user.userInfo.id, "MIDDLE_OP", 200, 80, middle)
                seedTokensUsage(user.userInfo.id, "NEWEST_OP", 300, 90, newest)

                val response = apiClient.getRecords(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.data.first().operationType shouldBe "NEWEST_OP"
                response.body!!.data.last().operationType shouldBe "OLDEST_OP"
            }

            @Test
            @DisplayName("200 - should respect pagination parameters")
            fun shouldRespectPaginationParameters() {
                val user = mockAuthenticatedUser()

                repeat(5) { i ->
                    seedTokensUsage(user.userInfo.id, "OP_$i", 100, 50)
                }

                val response = apiClient.getRecords(page = 1, perPage = 2, user = user)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize 2
                response.body!!.pagination.totalResults shouldBe 5L
                response.body!!.pagination.totalPages shouldBe 3
            }

            @Test
            @DisplayName("200 - should return empty data when user has no records")
            fun shouldReturnEmptyDataWhenNoRecords() {
                val user = mockAuthenticatedUser()

                val response = apiClient.getRecords(user = user)

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.data shouldHaveSize 0
                response.body!!.pagination.totalResults shouldBe 0L
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            @DisplayName("401 - anonymous user cannot access records")
            fun anonymousUserCannotAccessRecords() {
                val response = apiClient.getRecords(user = null)

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[GET] /api/v1/gpt-tokens-usage/analytics - aggregated analytics")
    inner class GetAnalytics {

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            @DisplayName("200 - should return daily usage and usage by operation type")
            fun shouldReturnDailyUsageAndUsageByOperationType() {
                val user = mockAuthenticatedUser()

                val day1 = Instant.now().minus(2, ChronoUnit.DAYS)
                val day2 = Instant.now().minus(1, ChronoUnit.DAYS)

                seedTokensUsage(user.userInfo.id, "CONVERSATION_AI_RESPONSE", 100, 50, day1)
                seedTokensUsage(user.userInfo.id, "CONVERSATION_AI_RESPONSE", 200, 80, day1)
                seedTokensUsage(user.userInfo.id, "WORD_EXPLANATION", 150, 60, day2)
                seedTokensUsage(user.userInfo.id, "WORD_EXPLANATION", 250, 90, day2)

                val response = apiClient.getAnalytics(
                    period = AnalyticsPeriod.ALL_TIME,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                response.body!!.dailyUsage shouldHaveSize 2
                response.body!!.usageByOperationType shouldHaveSize 2

                val conversationUsage = response.body!!.usageByOperationType
                    .find { it.operationType == "CONVERSATION_AI_RESPONSE" }
                conversationUsage shouldNotBe null
                conversationUsage!!.inputTokens shouldBe 300L
                conversationUsage.outputTokens shouldBe 130L
                conversationUsage.count shouldBe 2L

                val wordUsage = response.body!!.usageByOperationType
                    .find { it.operationType == "WORD_EXPLANATION" }
                wordUsage shouldNotBe null
                wordUsage!!.inputTokens shouldBe 400L
                wordUsage.outputTokens shouldBe 150L
                wordUsage.count shouldBe 2L
            }

            @Test
            @DisplayName("200 - should filter data by LAST_7_DAYS period")
            fun shouldFilterDataByLast7DaysPeriod() {
                val user = mockAuthenticatedUser()

                val recentDate = Instant.now().minus(3, ChronoUnit.DAYS)
                val oldDate = Instant.now().minus(15, ChronoUnit.DAYS)

                seedTokensUsage(user.userInfo.id, "RECENT_OP", 100, 50, recentDate)
                seedTokensUsage(user.userInfo.id, "OLD_OP", 200, 80, oldDate)

                val response = apiClient.getAnalytics(
                    period = AnalyticsPeriod.LAST_7_DAYS,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                response.body!!.dailyUsage shouldHaveSize 1
                response.body!!.dailyUsage.first().inputTokens shouldBe 100L
                response.body!!.dailyUsage.first().outputTokens shouldBe 50L

                response.body!!.usageByOperationType shouldHaveSize 1
                response.body!!.usageByOperationType.first().operationType shouldBe "RECENT_OP"
            }

            @Test
            @DisplayName("200 - should return all data for ALL_TIME period")
            fun shouldReturnAllDataForAllTimePeriod() {
                val user = mockAuthenticatedUser()

                val recentDate = Instant.now().minus(1, ChronoUnit.DAYS)
                val oldDate = Instant.now().minus(100, ChronoUnit.DAYS)

                seedTokensUsage(user.userInfo.id, "RECENT_OP", 100, 50, recentDate)
                seedTokensUsage(user.userInfo.id, "OLD_OP", 200, 80, oldDate)

                val response = apiClient.getAnalytics(
                    period = AnalyticsPeriod.ALL_TIME,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                response.body!!.dailyUsage shouldHaveSize 2
                response.body!!.usageByOperationType shouldHaveSize 2

                val totalInput = response.body!!.usageByOperationType.sumOf { it.inputTokens }
                val totalOutput = response.body!!.usageByOperationType.sumOf { it.outputTokens }
                totalInput shouldBe 300L
                totalOutput shouldBe 130L
            }

            @Test
            @DisplayName("200 - should return empty lists when user has no records")
            fun shouldReturnEmptyListsWhenNoRecords() {
                val user = mockAuthenticatedUser()

                val response = apiClient.getAnalytics(
                    period = AnalyticsPeriod.ALL_TIME,
                    user = user
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.dailyUsage shouldHaveSize 0
                response.body!!.usageByOperationType shouldHaveSize 0
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            @DisplayName("401 - anonymous user cannot access analytics")
            fun anonymousUserCannotAccessAnalytics() {
                val response = apiClient.getAnalytics(
                    period = AnalyticsPeriod.ALL_TIME,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }
}
