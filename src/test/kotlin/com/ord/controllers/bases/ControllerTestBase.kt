package com.ord.controllers.bases

import com.github.javafaker.Faker
import com.ord.config.properties.JwtProperties
import com.ord.core.auth.api.requests.dto.OtpVerifyDto
import com.ord.core.auth.models.OtpCodeEntity
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.models.GptTokensUsageEntity
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.shared.utils.EnumUtils.getRandomValue
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant
import java.util.*

abstract class ControllerTestBase(
    val webClient: WebTestClient,
    val jwtProperties: JwtProperties,
    val languageProficiencyRepository: LanguageProficiencyRepository,
    val userRepository: UserRepository,
    val otpCodeRepository: OtpCodeRepository,
    val passwordEncoder: PasswordEncoder,
    val gptTokensUsageRepository: GptTokensUsageRepository,
) : TestcontainersConfig() {
    val faker = Faker()

    fun mockAuthenticatedUser(
        email: String = faker.internet().emailAddress(),
        nativeLanguage: LanguageName = LanguageName.ENGLISH,
        languages: Map<LanguageName, LanguageProficiencyLevel> = mapOf(
            LanguageName.ENGLISH to LanguageProficiencyLevel.C1
        )
    ): MockedAuthenticatedUser {
        // Step 1: Create user in database
        userRepository.save(
            UserEntity(
                name = "Test User",
                email = email,
                nativeLanguage = nativeLanguage,
                selectedLearningLanguage = languages.keys.firstOrNull(),
                isAccountInitialized = true
            )
        ).block()!!

        // Step 2: Mock OTP code directly in database (faster than requesting)
        otpCodeRepository.save(
            OtpCodeEntity(
                code = passwordEncoder.encode("000000"),
                expiresAt = Instant.now().plusSeconds(600), // 10 minutes
                userEmail = email
            )
        ).block()

        // Step 3: Verify OTP (code is "000000")
        val response = webClient
            .post()
            .uri("/api/v1/auth/otp-verify")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                OtpVerifyDto(
                    email = email,
                    code = "000000"
                )
            )
            .exchange()
            .expectBody(UserDTO::class.java)
            .returnResult()

        val authCookie: ResponseCookie = response.responseCookies[jwtProperties.authCookieName]?.firstOrNull() ?: run {
            throw IllegalStateException("Failed to get the auth cookie from the response")
        }

        val userId = response.responseBody!!.id

        // Step 4: Create language proficiencies
        languageProficiencyRepository
            .saveAll(
                languages.map { (language, level) ->
                    LanguageProficiencyEntity(
                        userId = userId,
                        language = language,
                        level = level,
                        translateTo = LanguageName.ENGLISH,
                        generativeContentLanguage = language,
                    )
                }
            )
            .collectList()
            .block()

        return MockedAuthenticatedUser(
            token = authCookie.value,
            userInfo = response.responseBody!!,
            authCookie = authCookie,
            email = email
        )
    }

    fun mockAuthenticatedUserWithUninitializedAccount(
        email: String = faker.internet().emailAddress()
    ): MockedAuthenticatedUser {
        // Step 1: Create user in database with uninitialized account
        userRepository.save(
            UserEntity(
                name = "",
                email = email,
                nativeLanguage = null,
                selectedLearningLanguage = null,
                isAccountInitialized = false
            )
        ).block()!!

        // Step 2: Mock OTP code directly in database (faster than requesting)
        otpCodeRepository.save(
            OtpCodeEntity(
                code = passwordEncoder.encode("000000"),
                expiresAt = Instant.now().plusSeconds(600), // 10 minutes
                userEmail = email
            )
        ).block()

        // Step 3: Verify OTP (code is "000000")
        val response = webClient
            .post()
            .uri("/api/v1/auth/otp-verify")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(
                OtpVerifyDto(
                    email = email,
                    code = "000000"
                )
            )
            .exchange()
            .expectBody(UserDTO::class.java)
            .returnResult()

        val authCookie: ResponseCookie = response.responseCookies[jwtProperties.authCookieName]?.firstOrNull() ?: run {
            throw IllegalStateException("Failed to get the auth cookie from the response")
        }

        return MockedAuthenticatedUser(
            token = authCookie.value,
            userInfo = response.responseBody!!,
            authCookie = authCookie,
            email = email
        )
    }

    fun assertGptTokensLogCreated(userId: UUID, operationType: String) {
        val tokenUsage = gptTokensUsageRepository
            .findAllByUserId(userId)
            .collectList()
            .block()!!
            .find { it.operationType == operationType }

        tokenUsage shouldNotBe null
        tokenUsage!!.apply {
            inputTokens shouldBeGreaterThan 0
            outputTokens shouldBeGreaterThan 0
            model shouldBe GptTokensUsageEntity.DEFAULT_MODEL
        }
    }
}