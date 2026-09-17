package com.ord.controllers.auth

import com.ord.config.properties.SessionProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.api.requests.dto.OtpRequestDto
import com.ord.core.auth.api.requests.dto.OtpVerifyDto
import com.ord.core.auth.models.OtpCodeEntity
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.security.SessionTokenService
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
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import java.time.Instant

@DisplayName("- AuthController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "3600000")
class TestAuthController @Autowired constructor(
    private val userSessionRepository: UserSessionRepositoryReactive,
    private val sessionTokenService: SessionTokenService,
    webClient: WebTestClient,
    sessionProperties: SessionProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    aiProviderUsageRepository: AiProviderUsageRepository
) : ControllerTestBase(
    webClient,
    sessionProperties = sessionProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    aiProviderUsageRepository = aiProviderUsageRepository
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

    private fun findSession(rawToken: String) =
        userSessionRepository.findByTokenHash(sessionTokenService.hash(rawToken)).block()

    private fun sessionCookie(rawToken: String) =
        ResponseCookie.from(sessionProperties.cookieName, rawToken).build()

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
                        code = passwordEncoder.encode(TestData.OTP_CODE)!!,
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
                val authCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
                authCookie.shouldNotBeNull()

                val createdUser = userRepository.findByEmail(TestData.TEST_EMAIL).block()
                val session = findSession(authCookie.value)
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
                val authCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
                authCookie.shouldNotBeNull()

                val session = findSession(authCookie.value)
                session.shouldNotBeNull()
            }

            @Test
            fun `200 - cookie has been set with HttpOnly and Secure flags`() {
                val authCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()

                authCookie.shouldNotBeNull()
                authCookie.isHttpOnly shouldBe true
            }

            @Test
            fun `200 - should delete OTP after successful verification`() {
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldBeNull()
            }

            @Test
            fun `200 - should create a new session on each login`() {
                val authCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
                authCookie.shouldNotBeNull()

                val session = findSession(authCookie.value)
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
                        code = passwordEncoder.encode(TestData.OTP_CODE)!!,
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
                findSession(authenticatedUser.token).shouldNotBeNull()

                val response = authAPIClient.logout(user = authenticatedUser)

                response.status shouldBe HttpStatus.NO_CONTENT

                findSession(authenticatedUser.token).shouldBeNull()

                val authCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
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

            @Test
            fun `403 - should reject logout from a disallowed origin`() {
                val authenticatedUser = mockAuthenticatedUser()

                webClient
                    .mutate()
                    .defaultHeaders { headers -> headers.set(HttpHeaders.ORIGIN, "https://evil.example") }
                    .build()
                    .delete()
                    .uri("/api/v1/auth/logout")
                    .cookie(authenticatedUser.authCookie.name, authenticatedUser.authCookie.value)
                    .exchange()
                    .expectStatus().isForbidden

                findSession(authenticatedUser.token).shouldNotBeNull()
            }

            @Test
            fun `403 - should reject logout without an origin`() {
                val authenticatedUser = mockAuthenticatedUser()

                webClient
                    .mutate()
                    .defaultHeaders { headers -> headers.remove(HttpHeaders.ORIGIN) }
                    .build()
                    .delete()
                    .uri("/api/v1/auth/logout")
                    .cookie(authenticatedUser.authCookie.name, authenticatedUser.authCookie.value)
                    .exchange()
                    .expectStatus().isForbidden

                findSession(authenticatedUser.token).shouldNotBeNull()
            }
        }
    }

    @Nested
    @DisplayName("Opaque session cookie")
    inner class OpaqueSessionTests {

        private fun persistUser(): UserEntity {
            return userRepository.save(
                UserEntity(
                    name = "Test User",
                    email = TestData.TEST_EMAIL,
                    nativeLanguage = LanguageName.ENGLISH,
                    selectedLearningLanguage = LanguageName.SPANISH,
                    isAccountInitialized = true
                )
            ).block()!!
        }

        private fun persistSession(
            user: UserEntity,
            rawToken: String = sessionTokenService.generateRawToken(),
            idleExpiresAt: Instant = Instant.now().plus(Duration.ofDays(7)),
            absoluteExpiresAt: Instant = Instant.now().plus(Duration.ofDays(30)),
        ): Pair<String, com.ord.core.auth.models.UserSessionEntity> {
            val now = Instant.now()
            val session = userSessionRepository.save(
                com.ord.core.auth.models.UserSessionEntity(
                    tokenHash = sessionTokenService.hash(rawToken),
                    userId = user.id!!,
                    createdAt = now,
                    lastSeenAt = now,
                    idleExpiresAt = idleExpiresAt,
                    absoluteExpiresAt = absoluteExpiresAt,
                )
            ).block()!!

            return Pair(rawToken, session)
        }

        private fun userWithCookie(user: UserEntity, rawToken: String) =
            com.ord.testing_utils.dto.MockedAuthenticatedUser(
                token = rawToken,
                userInfo = user.toDTO(),
                authCookie = sessionCookie(rawToken),
                email = user.email
            )

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should authenticate with the same opaque cookie across sequential requests`() {
                val user = persistUser()
                val (rawToken, _) = persistSession(user)
                val mockUser = userWithCookie(user, rawToken)

                val firstResponse = usersAPIClient.me(user = mockUser)
                firstResponse.status shouldBe HttpStatus.OK
                firstResponse.body.shouldNotBeNull()
                firstResponse.body.email shouldBe TestData.TEST_EMAIL
                firstResponse.cookies[sessionProperties.cookieName]?.firstOrNull().shouldBeNull()

                val secondResponse = usersAPIClient.me(user = mockUser)
                secondResponse.status shouldBe HttpStatus.OK
                secondResponse.body.shouldNotBeNull()
                secondResponse.body.email shouldBe TestData.TEST_EMAIL
                findSession(rawToken).shouldNotBeNull()
            }

            @Test
            fun `200 - should persist only the hash of the session cookie`() {
                val user = persistUser()
                val (rawToken, session) = persistSession(user)

                session.tokenHash shouldBe sessionTokenService.hash(rawToken)
                session.tokenHash shouldNotBe rawToken

                val response = usersAPIClient.me(user = userWithCookie(user, rawToken))
                response.status shouldBe HttpStatus.OK
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should fail when the cookie has no matching session`() {
                val user = persistUser()
                val rawToken = sessionTokenService.generateRawToken()

                val response = usersAPIClient.me(user = userWithCookie(user, rawToken))
                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `401 - should fail and clear the cookie when the session is idle-expired`() {
                val user = persistUser()
                val (rawToken, _) = persistSession(
                    user = user,
                    idleExpiresAt = Instant.now().minusSeconds(60),
                )

                val response = usersAPIClient.me(user = userWithCookie(user, rawToken))
                response.status shouldBe HttpStatus.UNAUTHORIZED

                val clearedCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
                clearedCookie.shouldNotBeNull()
                clearedCookie.maxAge.seconds shouldBe 0
                findSession(rawToken).shouldBeNull()
            }

            @Test
            fun `401 - should fail when the session exceeded absolute timeout`() {
                val user = persistUser()
                val (rawToken, _) = persistSession(
                    user = user,
                    absoluteExpiresAt = Instant.now().minusSeconds(60),
                )

                val response = usersAPIClient.me(user = userWithCookie(user, rawToken))
                response.status shouldBe HttpStatus.UNAUTHORIZED
                findSession(rawToken).shouldBeNull()
            }
        }
    }
}
