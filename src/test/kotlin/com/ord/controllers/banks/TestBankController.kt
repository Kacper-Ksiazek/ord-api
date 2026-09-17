package com.ord.controllers.banks

import com.ord.config.properties.SessionProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import com.ord.features.bank.repository.BankRepository
import com.ord.features.bank_group.repository.BankGroupRepository
import com.ord.seeders.entities.BankGroupSeeder
import com.ord.seeders.entities.BankSeeder
import com.ord.testing_utils.api.clients.BanksAPIClient
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient

@DisplayName("- BankController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestBankController @Autowired constructor(
    private val bankRepository: BankRepository,
    private val bankGroupRepository: BankGroupRepository,
    private val bankSeeder: BankSeeder,
    private val bankGroupSeeder: BankGroupSeeder,
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
    private val banksAPIClient = BanksAPIClient(webClient)

    @AfterEach
    fun cleanup() {
        bankRepository.deleteAll().block()
        bankGroupRepository.deleteAll().block()
    }

    @Nested
    @DisplayName("[GET] /api/v1/banks")
    inner class ListBanks {

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should reject unauthenticated request`() {
                val response = banksAPIClient.listBanks(user = null)

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return only banks owned by the authenticated user`() {
                val userA = mockAuthenticatedUser()
                val userB = mockAuthenticatedUser()

                bankSeeder.seedOneEntityForUser(user = userA.userInfo)
                bankSeeder.seedOneEntityForUser(user = userA.userInfo)
                bankSeeder.seedOneEntityForUser(user = userB.userInfo)

                val responseA = banksAPIClient.listBanks(user = userA)
                val responseB = banksAPIClient.listBanks(user = userB)

                responseA.status shouldBe HttpStatus.OK
                responseA.body!!.size shouldBe 2

                responseB.status shouldBe HttpStatus.OK
                responseB.body!!.size shouldBe 1
            }

            @Test
            fun `200 - should include bank group name and color in response`() {
                val user = mockAuthenticatedUser()
                val bankGroup = bankGroupSeeder.seedOneEntityForUser(user = user.userInfo)
                val bank = bankSeeder.seedOneEntityForUser(
                    user = user.userInfo,
                    bankGroup = bankGroup,
                )

                val response = banksAPIClient.listBanks(user = user)

                response.status shouldBe HttpStatus.OK
                response.body!!.single().id shouldBe bank.id
                response.body.single().name shouldBe bank.name
                response.body.single().bankGroup!!.name shouldBe bankGroup.name
                response.body.single().bankGroup!!.color shouldBe bankGroup.color
            }
        }
    }
}
