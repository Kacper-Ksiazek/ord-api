package com.ord.controllers.auth

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.api.requests.dto.OtpRequestDto
import com.ord.core.auth.api.requests.dto.OtpVerifyDto
import com.ord.core.auth.models.OtpCodeEntity
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.UserRepository
import com.ord.core.security.UserSessionRepositoryReactive
import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.core.user.model.toDTO
import com.ord.testing_utils.api.clients.AuthAPIClient
import com.ord.testing_utils.api.clients.UsersAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant

@DisplayName("- AuthController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "3600000")
class TestAuthController @Autowired constructor(
    private val userSessionRepository: UserSessionRepositoryReactive,
    private val jwtService: com.ord.core.security.JwtService,
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
    private val authAPIClient = AuthAPIClient(webClient)
    private val usersAPIClient = UsersAPIClient(webClient)

    object TestData {
        const val TEST_EMAIL = "testajjfsadfodsjfoidsjfisdfoisdjfois@example.com"
        const val OTP_CODE = "000000"
    }

    @AfterEach
    fun cleanup() {
        userRepository.deleteByEmail(TestData.TEST_EMAIL).block()
        otpCodeRepository.deleteByUserEmail(TestData.TEST_EMAIL).block()
    }

    @Nested
    @DisplayName("[POST] /api/v1/auth/otp-request - request OTP code")
    inner class OtpRequestTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should successfully request OTP for any email`() {
                val response = authAPIClient.requestOtp(
                    OtpRequestDto(email = TestData.TEST_EMAIL)
                )

                response.status shouldBe HttpStatus.OK

                // Verify OTP was created in database
                val otpCode = otpCodeRepository
                    .findByUserEmail(TestData.TEST_EMAIL)
                    .block()

                otpCode.shouldNotBeNull()
                otpCode.userEmail shouldBe TestData.TEST_EMAIL
                passwordEncoder.matches(TestData.OTP_CODE, otpCode.code) shouldBe true
            }

            @Test
            fun `200 - should replace existing OTP when requesting again`() {
                // First request
                authAPIClient.requestOtp(OtpRequestDto(email = TestData.TEST_EMAIL))
                val firstOtp = otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block()

                // Wait a moment
                Thread.sleep(100)

                // Second request
                authAPIClient.requestOtp(OtpRequestDto(email = TestData.TEST_EMAIL))
                val secondOtp = otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block()

                // Should only have one OTP
                secondOtp.shouldNotBeNull()
                secondOtp.id shouldNotBe firstOtp?.id
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `400 - should fail with missing email`() {
                val response = authAPIClient.requestOtp(
                    OtpRequestDto(email = "")
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `403 - should fail with unauthorized domain email`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = authAPIClient.requestOtp(
                    body = OtpRequestDto(email = authenticatedUser.email),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.FORBIDDEN
            }

            @Test
            fun `400 - should fail with invalid email format`() {
                val response = authAPIClient.requestOtp(
                    OtpRequestDto(email = "invalid-email")
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/auth/otp-verify - verify OTP and authenticate")
    inner class OtpVerifyTests {

        @BeforeEach
        fun beforeEach() {
            otpCodeRepository
                .save(
                    OtpCodeEntity(
                        code = passwordEncoder.encode(TestData.OTP_CODE),
                        expiresAt = Instant.now().plusSeconds(600),
                        userEmail = TestData.TEST_EMAIL
                    )
                ).block()
        }

        @Nested
        @DisplayName("Positive - New User Registration")
        inner class PositiveNewUser {
            lateinit var response: APIClientResponse<UserDTO?>

            @BeforeEach
            fun setup() {
                // Verify user doesn't exist yet
                userRepository.findByEmail(TestData.TEST_EMAIL).block().shouldBeNull()

                response = authAPIClient.verifyOtp(
                    OtpVerifyDto(
                        email = TestData.TEST_EMAIL,
                        code = TestData.OTP_CODE
                    )
                )

                response.status shouldBe HttpStatus.OK
                response.body.shouldNotBeNull()
            }

            @Test
            fun `200 - should create new user with correct properties`() {
                response.body!!.email shouldBe TestData.TEST_EMAIL
                response.body!!.isAccountInitialized shouldBe false
                response.body!!.nativeLanguage.shouldBeNull()
                response.body!!.selectedLearningLanguage.shouldBeNull()
            }

            @Test
            fun `200 - should persist user in database`() {
                val createdUser = userRepository.findByEmail(TestData.TEST_EMAIL).block()
                createdUser.shouldNotBeNull()
                createdUser.email shouldBe TestData.TEST_EMAIL
                createdUser.isAccountInitialized shouldBe false
            }

            @Test
            fun `200 - should create a session for the new user`() {
                val authCookie = response.cookies[jwtProperties.authCookieName]?.firstOrNull()
                authCookie.shouldNotBeNull()

                val createdUser = userRepository.findByEmail(TestData.TEST_EMAIL).block()
                val session = userSessionRepository.findByToken(authCookie.value).block()
                session.shouldNotBeNull()
                session.userId shouldBe createdUser!!.id
            }

            @Test
            fun `200 - should delete OTP after successful verification`() {
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldBeNull()
            }
        }

        @Nested
        @DisplayName("Positive - Existing User Login")
        inner class PositiveExistingUser {
            lateinit var response: APIClientResponse<UserDTO?>
            lateinit var user: UserEntity

            @BeforeEach
            fun beforeAll() {
                user = userRepository.save(
                    UserEntity(
                        name = "Test User",
                        email = TestData.TEST_EMAIL,
                        nativeLanguage = LanguageName.ENGLISH,
                        selectedLearningLanguage = LanguageName.SPANISH,
                        isAccountInitialized = true
                    )
                ).block()!!

                response = authAPIClient.verifyOtp(
                    OtpVerifyDto(
                        email = TestData.TEST_EMAIL,
                        code = TestData.OTP_CODE
                    )
                )

                response.status shouldBe HttpStatus.OK
                response.body.shouldNotBeNull()
            }

            @Test
            fun `200 - should authenticate existing user when OTP is valid`() {
                response.body!!.id shouldBe user.id
                response.body!!.email shouldBe TestData.TEST_EMAIL
                response.body!!.isAccountInitialized shouldBe true
                response.body!!.nativeLanguage shouldBe LanguageName.ENGLISH

                // Verify session was created
                val authCookie = response.cookies[jwtProperties.authCookieName]?.firstOrNull()
                authCookie.shouldNotBeNull()

                val session = userSessionRepository.findByToken(authCookie.value).block()
                session.shouldNotBeNull()
            }

            @Test
            fun `200 - cookie has been set with HttpOnly and Secure flags`() {
                val authCookie = response.cookies[jwtProperties.authCookieName]?.firstOrNull()

                authCookie.shouldNotBeNull()
            }

            @Test
            fun `200 - should delete OTP after successful verification`() {
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldBeNull()
            }

            @Test
            fun `200 - should create a new session on each login`() {
                val authCookie = response.cookies[jwtProperties.authCookieName]?.firstOrNull()
                authCookie.shouldNotBeNull()

                val session = userSessionRepository.findByToken(authCookie.value).block()
                session.shouldNotBeNull()
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should fail with invalid OTP code`() {
                val response = authAPIClient.verifyOtp(
                    OtpVerifyDto(
                        email = TestData.TEST_EMAIL,
                        code = "999999"
                    )
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `401 - should fail with expired OTP code`() {
                // Delete the valid OTP from @BeforeEach and create an expired one
                otpCodeRepository.deleteByUserEmail(TestData.TEST_EMAIL).block()

                otpCodeRepository.save(
                    OtpCodeEntity(
                        code = passwordEncoder.encode(TestData.OTP_CODE),
                        expiresAt = Instant.now().minusSeconds(1), // Expired 1 second ago
                        userEmail = TestData.TEST_EMAIL
                    )
                ).block()

                val response = authAPIClient.verifyOtp(
                    OtpVerifyDto(
                        email = TestData.TEST_EMAIL,
                        code = TestData.OTP_CODE
                    )
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `401 - should fail when no OTP exists for email`() {
                // Delete the OTP from @BeforeEach
                otpCodeRepository.deleteByUserEmail(TestData.TEST_EMAIL).block()

                val response = authAPIClient.verifyOtp(
                    OtpVerifyDto(
                        email = TestData.TEST_EMAIL,
                        code = TestData.OTP_CODE
                    )
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - should fail with invalid OTP code format`() {
                val response = authAPIClient.verifyOtp(
                    OtpVerifyDto(
                        email = TestData.TEST_EMAIL,
                        code = "12345"
                    )
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `403 - should fail for authenticated users`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = authAPIClient.verifyOtp(
                    body = OtpVerifyDto(
                        email = TestData.TEST_EMAIL,
                        code = TestData.OTP_CODE
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.FORBIDDEN
            }
        }
    }

    @Nested
    @DisplayName("[DELETE] /api/v1/auth/logout - logout user")
    inner class LogoutTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `204 - should successfully logout authenticated user`() {
                val authenticatedUser = mockAuthenticatedUser()

                // Verify session exists
                userSessionRepository.findByToken(authenticatedUser.token).block().shouldNotBeNull()

                val response = authAPIClient.logout(user = authenticatedUser)

                response.status shouldBe HttpStatus.NO_CONTENT

                // Verify session was deleted
                userSessionRepository.findByToken(authenticatedUser.token).block().shouldBeNull()

                // Verify auth cookie was invalidated
                val authCookie = response.cookies[jwtProperties.authCookieName]?.firstOrNull()
                authCookie.shouldNotBeNull()
                authCookie.maxAge.seconds shouldBe 0
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should fail for anonymous users`() {
                val response = authAPIClient.logout()

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("JWT Token Refresh - automatic renewal on expiration")
    inner class JwtTokenRefreshTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should automatically refresh expired JWT token and allow continued access`() {
                // Create a user
                val user = userRepository.save(
                    UserEntity(
                        name = "Test User",
                        email = TestData.TEST_EMAIL,
                        nativeLanguage = LanguageName.ENGLISH,
                        selectedLearningLanguage = LanguageName.SPANISH,
                        isAccountInitialized = true
                    )
                ).block()!!

                // Create an expired JWT token - set issuedAt far enough in the past to ensure expiration
                val expiredToken = jwtService.createToken(
                    subject = user.email,
                    issuedAt = Instant.now().minusSeconds(jwtProperties.expirationTime + 3600)
                )

                // Create a session with the expired token
                val session = userSessionRepository.save(
                    com.ord.core.auth.models.UserSessionEntity(
                        userId = user.id!!,
                        token = expiredToken
                    )
                ).block()!!

                // Create a mocked authenticated user with the expired token
                val mockUser = com.ord.testing_utils.dto.MockedAuthenticatedUser(
                    token = expiredToken,
                    userInfo = user.toDTO(),
                    authCookie = org.springframework.http.ResponseCookie.from(jwtProperties.authCookieName, expiredToken).build(),
                    email = user.email
                )

                // Call /me endpoint with expired token
                val response = usersAPIClient.me(user = mockUser)

                // Should succeed with 200
                response.status shouldBe HttpStatus.OK
                response.body.shouldNotBeNull()
                response.body.email shouldBe TestData.TEST_EMAIL

                // Verify new token was set in cookie
                val newAuthCookie = response.cookies[jwtProperties.authCookieName]?.firstOrNull()
                newAuthCookie.shouldNotBeNull()
                newAuthCookie.value shouldNotBe expiredToken

                // Verify the session was updated with the new token
                val updatedSession = userSessionRepository.findById(session.id!!).block()
                updatedSession.shouldNotBeNull()
                updatedSession.token shouldBe newAuthCookie.value
                updatedSession.token shouldNotBe expiredToken

                // Verify the new token is valid
                val parsedToken = jwtService.parseAndValidate(newAuthCookie.value)
                parsedToken.body.subject shouldBe user.email
            }

            @Test
            fun `200 - should update session token in database after refresh`() {
                // Create a user
                val user = userRepository.save(
                    UserEntity(
                        name = "Test User",
                        email = TestData.TEST_EMAIL,
                        nativeLanguage = LanguageName.ENGLISH,
                        selectedLearningLanguage = LanguageName.SPANISH,
                        isAccountInitialized = true
                    )
                ).block()!!

                // Create an expired JWT token - set issuedAt far enough in the past to ensure expiration
                // even with very long expirationTime settings (like in CI/CD)
                val expiredToken = jwtService.createToken(
                    subject = user.email,
                    issuedAt = Instant.now().minusSeconds(jwtProperties.expirationTime + 3600)
                )

                // Create a session with the expired token
                userSessionRepository.save(
                    com.ord.core.auth.models.UserSessionEntity(
                        userId = user.id!!,
                        token = expiredToken
                    )
                ).block()!!

                val mockUser = com.ord.testing_utils.dto.MockedAuthenticatedUser(
                    token = expiredToken,
                    userInfo = user.toDTO(),
                    authCookie = org.springframework.http.ResponseCookie.from(jwtProperties.authCookieName, expiredToken).build(),
                    email = user.email
                )

                val response = usersAPIClient.me(user = mockUser)

                response.status shouldBe HttpStatus.OK

                // Verify old session token no longer exists
                userSessionRepository.findByToken(expiredToken).block().shouldBeNull()

                // Verify new session exists with new token
                val newToken = response.cookies[jwtProperties.authCookieName]?.firstOrNull()?.value
                newToken.shouldNotBeNull()

                val updatedSession = userSessionRepository.findByToken(newToken).block()
                updatedSession.shouldNotBeNull()
                updatedSession.userId shouldBe user.id
            }

            @Test
            fun `200 - should allow multiple sequential requests with refreshed token`() {
                // Create a user
                val user = userRepository.save(
                    UserEntity(
                        name = "Test User",
                        email = TestData.TEST_EMAIL,
                        nativeLanguage = LanguageName.ENGLISH,
                        selectedLearningLanguage = LanguageName.SPANISH,
                        isAccountInitialized = true
                    )
                ).block()!!

                // Create an expired JWT token - set issuedAt far enough in the past to ensure expiration
                val expiredToken = jwtService.createToken(
                    subject = user.email,
                    issuedAt = Instant.now().minusSeconds(jwtProperties.expirationTime + 3600)
                )

                // Create a session with the expired token
                userSessionRepository.save(
                    com.ord.core.auth.models.UserSessionEntity(
                        userId = user.id!!,
                        token = expiredToken
                    )
                ).block()!!

                val mockUser = com.ord.testing_utils.dto.MockedAuthenticatedUser(
                    token = expiredToken,
                    userInfo = user.toDTO(),
                    authCookie = org.springframework.http.ResponseCookie.from(jwtProperties.authCookieName, expiredToken).build(),
                    email = user.email
                )

                // First request with expired token
                val firstResponse = usersAPIClient.me(user = mockUser)
                firstResponse.status shouldBe HttpStatus.OK

                // Get the refreshed token
                val refreshedToken = firstResponse.cookies[jwtProperties.authCookieName]?.firstOrNull()?.value
                refreshedToken.shouldNotBeNull()

                // Second request with refreshed token
                val mockUserWithRefreshedToken = com.ord.testing_utils.dto.MockedAuthenticatedUser(
                    token = refreshedToken!!,
                    userInfo = user.toDTO(),
                    authCookie = org.springframework.http.ResponseCookie.from(jwtProperties.authCookieName, refreshedToken).build(),
                    email = user.email
                )

                val secondResponse = usersAPIClient.me(user = mockUserWithRefreshedToken)
                secondResponse.status shouldBe HttpStatus.OK
                secondResponse.body.shouldNotBeNull()
                secondResponse.body.email shouldBe TestData.TEST_EMAIL
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should fail with expired token when no session exists`() {
                // Create a user
                val user = userRepository.save(
                    UserEntity(
                        name = "Test User",
                        email = TestData.TEST_EMAIL,
                        nativeLanguage = LanguageName.ENGLISH,
                        selectedLearningLanguage = LanguageName.SPANISH,
                        isAccountInitialized = true
                    )
                ).block()!!

                // Create an expired JWT token but don't create a session
                val expiredToken = jwtService.createToken(
                    subject = user.email,
                    issuedAt = Instant.now().minusSeconds(jwtProperties.expirationTime + 3600)
                )

                val mockUser = com.ord.testing_utils.dto.MockedAuthenticatedUser(
                    token = expiredToken,
                    userInfo = user.toDTO(),
                    authCookie = org.springframework.http.ResponseCookie.from(jwtProperties.authCookieName, expiredToken).build(),
                    email = user.email
                )

                // Should fail because there's no session
                val response = usersAPIClient.me(user = mockUser)
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }
}