package com.ord.controllers.auth

import com.ord.config.properties.SessionProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.auth.api.requests.dto.OtpRequestDto
import com.ord.core.auth.api.requests.dto.OtpVerifyDto
import com.ord.core.auth.models.OtpCodeEntity
import com.ord.core.auth.models.UserSessionEntity
import com.ord.core.auth.repositories.OtpCodeRepository
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
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture

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
        const val DISALLOWED_ORIGIN = "https://evil.example"
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

    private fun webClientWithOrigin(origin: String?): WebTestClient =
        webClient.mutate().defaultHeaders { headers ->
            if (origin == null) {
                headers.remove(HttpHeaders.ORIGIN)
            } else {
                headers.set(HttpHeaders.ORIGIN, origin)
            }
        }.build()

    private fun uniqueEmail() = "auth-edge-${UUID.randomUUID()}@example.com"

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
                authAPIClient.requestOtp(OtpRequestDto(email = TestData.TEST_EMAIL))
                val firstOtp = otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block()
                firstOtp.shouldNotBeNull()

                // Test profile always generates plaintext 000000, so overwrite the first hash
                // to bcrypt(111111) before the second request. That lets us prove the old code dies.
                otpCodeRepository.save(
                    firstOtp.copy(code = passwordEncoder.encode("111111")!!)
                ).block()

                authAPIClient.requestOtp(OtpRequestDto(email = TestData.TEST_EMAIL))
                val secondOtp = otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block()

                secondOtp.shouldNotBeNull()
                secondOtp.id shouldNotBe firstOtp.id
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block()!!.id shouldBe secondOtp.id

                val oldCodeResponse = authAPIClient.verifyOtp(
                    OtpVerifyDto(email = TestData.TEST_EMAIL, code = "111111")
                )
                oldCodeResponse.status shouldBe HttpStatus.UNAUTHORIZED

                val currentCodeResponse = authAPIClient.verifyOtp(
                    OtpVerifyDto(email = TestData.TEST_EMAIL, code = TestData.OTP_CODE)
                )
                currentCodeResponse.status shouldBe HttpStatus.OK
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

            @Test
            fun `403 - should reject otp-request from a disallowed origin`() {
                val email = uniqueEmail()
                try {
                    val response = AuthAPIClient(webClientWithOrigin(TestData.DISALLOWED_ORIGIN))
                        .requestOtp(OtpRequestDto(email = email))

                    response.status shouldBe HttpStatus.FORBIDDEN
                    otpCodeRepository.findByUserEmail(email).block().shouldBeNull()
                } finally {
                    otpCodeRepository.deleteByUserEmail(email).block()
                    userRepository.deleteByEmail(email).block()
                }
            }

            @Test
            fun `403 - should reject otp-request without an origin`() {
                val email = uniqueEmail()
                try {
                    val response = AuthAPIClient(webClientWithOrigin(null))
                        .requestOtp(OtpRequestDto(email = email))

                    response.status shouldBe HttpStatus.FORBIDDEN
                    otpCodeRepository.findByUserEmail(email).block().shouldBeNull()
                } finally {
                    otpCodeRepository.deleteByUserEmail(email).block()
                    userRepository.deleteByEmail(email).block()
                }
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
            @DisplayName("200 - cookie is an opaque HttpOnly session cookie with Path=/ and SameSite=Lax")
            fun `200 - cookie is an opaque HttpOnly session cookie with root Path and SameSite=Lax`() {
                val authCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()

                authCookie.shouldNotBeNull()
                authCookie.name shouldBe sessionProperties.cookieName
                authCookie.isHttpOnly shouldBe true
                authCookie.path shouldBe "/"
                authCookie.sameSite shouldBe "Lax"
                authCookie.maxAge.isNegative shouldBe true
                authCookie.value.split('.').size shouldNotBe 3
            }

            @Test
            fun `200 - should delete OTP after successful verification`() {
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldBeNull()
            }

            @Test
            fun `200 - should create a new session on each login`() {
                val firstCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
                firstCookie.shouldNotBeNull()

                authAPIClient.requestOtp(OtpRequestDto(email = TestData.TEST_EMAIL))
                val secondResponse = authAPIClient.verifyOtp(
                    OtpVerifyDto(
                        email = TestData.TEST_EMAIL,
                        code = TestData.OTP_CODE
                    )
                )
                secondResponse.status shouldBe HttpStatus.OK

                val secondCookie = secondResponse.cookies[sessionProperties.cookieName]?.firstOrNull()
                secondCookie.shouldNotBeNull()
                secondCookie.value shouldNotBe firstCookie.value

                findSession(firstCookie.value).shouldNotBeNull()
                findSession(secondCookie.value).shouldNotBeNull()

                val sessions = userSessionRepository.findAll().collectList().block()!!
                    .filter { it.userId == user.id }
                sessions shouldHaveSize 2

                usersAPIClient.me(user = userWithCookie(user, firstCookie.value)).status shouldBe HttpStatus.OK
                usersAPIClient.me(user = userWithCookie(user, secondCookie.value)).status shouldBe HttpStatus.OK
            }

            @Test
            fun `200 - should not return the raw session token in the body`() {
                val authCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
                authCookie.shouldNotBeNull()

                response.body.toString() shouldNotContain authCookie.value
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

            @Test
            fun `401 - should fail when reusing an already verified OTP`() {
                val firstResponse = authAPIClient.verifyOtp(
                    OtpVerifyDto(email = TestData.TEST_EMAIL, code = TestData.OTP_CODE)
                )
                firstResponse.status shouldBe HttpStatus.OK

                val secondResponse = authAPIClient.verifyOtp(
                    OtpVerifyDto(email = TestData.TEST_EMAIL, code = TestData.OTP_CODE)
                )
                secondResponse.status shouldBe HttpStatus.UNAUTHORIZED
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldBeNull()
            }

            @Test
            fun `401 - should still accept a valid OTP after a wrong code`() {
                val wrongResponse = authAPIClient.verifyOtp(
                    OtpVerifyDto(email = TestData.TEST_EMAIL, code = "999999")
                )
                wrongResponse.status shouldBe HttpStatus.UNAUTHORIZED
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldNotBeNull()

                val validResponse = authAPIClient.verifyOtp(
                    OtpVerifyDto(email = TestData.TEST_EMAIL, code = TestData.OTP_CODE)
                )
                validResponse.status shouldBe HttpStatus.OK
            }

            @Test
            fun `401 - should delete the OTP row when the code is expired`() {
                otpCodeRepository.deleteByUserEmail(TestData.TEST_EMAIL).block()

                otpCodeRepository.save(
                    OtpCodeEntity(
                        code = passwordEncoder.encode(TestData.OTP_CODE)!!,
                        expiresAt = Instant.now().minusSeconds(1),
                        userEmail = TestData.TEST_EMAIL
                    )
                ).block()

                val response = authAPIClient.verifyOtp(
                    OtpVerifyDto(email = TestData.TEST_EMAIL, code = TestData.OTP_CODE)
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldBeNull()
            }

            @Test
            fun `401 - should fail when the OTP belongs to a different email`() {
                val otherEmail = uniqueEmail()
                try {
                    val response = authAPIClient.verifyOtp(
                        OtpVerifyDto(email = otherEmail, code = TestData.OTP_CODE)
                    )

                    response.status shouldBe HttpStatus.UNAUTHORIZED
                    userRepository.findByEmail(otherEmail).block().shouldBeNull()
                    otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldNotBeNull()
                    response.cookies[sessionProperties.cookieName]?.firstOrNull().shouldBeNull()
                } finally {
                    userRepository.deleteByEmail(otherEmail).block()
                    otpCodeRepository.deleteByUserEmail(otherEmail).block()
                }
            }

            @Test
            fun `400 - should fail with invalid email format`() {
                val response = authAPIClient.verifyOtp(
                    OtpVerifyDto(email = "not-an-email", code = TestData.OTP_CODE)
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `403 - should reject otp-verify from a disallowed origin`() {
                val response = AuthAPIClient(webClientWithOrigin(TestData.DISALLOWED_ORIGIN)).verifyOtp(
                    OtpVerifyDto(email = TestData.TEST_EMAIL, code = TestData.OTP_CODE)
                )

                response.status shouldBe HttpStatus.FORBIDDEN
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldNotBeNull()
                userRepository.findByEmail(TestData.TEST_EMAIL).block().shouldBeNull()
                response.cookies[sessionProperties.cookieName]?.firstOrNull().shouldBeNull()
            }

            @Test
            fun `403 - should reject otp-verify without an origin`() {
                val response = AuthAPIClient(webClientWithOrigin(null)).verifyOtp(
                    OtpVerifyDto(email = TestData.TEST_EMAIL, code = TestData.OTP_CODE)
                )

                response.status shouldBe HttpStatus.FORBIDDEN
                otpCodeRepository.findByUserEmail(TestData.TEST_EMAIL).block().shouldNotBeNull()
                userRepository.findByEmail(TestData.TEST_EMAIL).block().shouldBeNull()
                response.cookies[sessionProperties.cookieName]?.firstOrNull().shouldBeNull()
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

            @Test
            @DisplayName("401 - should reject /me after logout with the old cookie")
            fun `401 - should reject me after logout with the old cookie`() {
                val authenticatedUser = mockAuthenticatedUser()

                val logoutResponse = authAPIClient.logout(user = authenticatedUser)
                logoutResponse.status shouldBe HttpStatus.NO_CONTENT

                val meResponse = usersAPIClient.me(user = authenticatedUser)
                meResponse.status shouldBe HttpStatus.UNAUTHORIZED
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

            @Test
            fun `401 - should fail when the cookie has no matching session`() {
                val authenticatedUser = mockAuthenticatedUser()
                userSessionRepository.deleteByTokenHash(sessionTokenService.hash(authenticatedUser.token)).block()

                val logoutResponse = authAPIClient.logout(user = authenticatedUser)
                logoutResponse.status shouldBe HttpStatus.UNAUTHORIZED
                val logoutCookie = logoutResponse.cookies[sessionProperties.cookieName]?.firstOrNull()
                logoutCookie.shouldNotBeNull()
                logoutCookie.maxAge.seconds shouldBe 0

                val meResponse = usersAPIClient.me(user = authenticatedUser)
                meResponse.status shouldBe HttpStatus.UNAUTHORIZED
                val meCookie = meResponse.cookies[sessionProperties.cookieName]?.firstOrNull()
                meCookie.shouldNotBeNull()
                meCookie.maxAge.seconds shouldBe 0
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
            lastSeenAt: Instant = Instant.now(),
        ): Pair<String, UserSessionEntity> {
            val now = Instant.now()
            val session = userSessionRepository.save(
                UserSessionEntity(
                    tokenHash = sessionTokenService.hash(rawToken),
                    userId = user.id!!,
                    createdAt = now,
                    lastSeenAt = lastSeenAt,
                    idleExpiresAt = idleExpiresAt,
                    absoluteExpiresAt = absoluteExpiresAt,
                )
            ).block()!!

            return Pair(rawToken, session)
        }

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

            @Test
            @DisplayName("200 - should keep the same cookie under parallel /me requests")
            fun `200 - should keep the same cookie under parallel me requests`() {
                val user = persistUser()
                val (rawToken, _) = persistSession(user)
                val mockUser = userWithCookie(user, rawToken)

                val responses = (1..8).map {
                    CompletableFuture.supplyAsync {
                        UsersAPIClient(webClient.mutate().build()).me(user = mockUser)
                    }
                }.map { it.join() }

                responses.forEach { response ->
                    response.status shouldBe HttpStatus.OK
                    val setCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
                    if (setCookie != null) {
                        setCookie.value shouldBe rawToken
                    }
                }
                findSession(rawToken).shouldNotBeNull()
            }

            @Test
            fun `200 - should slide idle expiry after the slide interval`() {
                val user = persistUser()
                val lastSeenAt = Instant.now().minus(Duration.ofMinutes(2))
                val (rawToken, _) = persistSession(user, lastSeenAt = lastSeenAt)
                val stored = findSession(rawToken)
                stored.shouldNotBeNull()

                val response = usersAPIClient.me(user = userWithCookie(user, rawToken))
                response.status shouldBe HttpStatus.OK
                response.cookies[sessionProperties.cookieName]?.firstOrNull().shouldBeNull()

                val after = findSession(rawToken)
                after.shouldNotBeNull()
                after.lastSeenAt shouldBeGreaterThan stored.lastSeenAt

                val expectedIdle = Instant.now().plus(sessionProperties.idleTimeout)
                (Duration.between(after.idleExpiresAt, expectedIdle).abs() < Duration.ofSeconds(5)) shouldBe true
            }

            @Test
            fun `200 - should not slide idle expiry inside the slide interval`() {
                val user = persistUser()
                val (rawToken, _) = persistSession(
                    user,
                    lastSeenAt = Instant.now().minusSeconds(5),
                )
                val stored = findSession(rawToken)
                stored.shouldNotBeNull()

                val response = usersAPIClient.me(user = userWithCookie(user, rawToken))
                response.status shouldBe HttpStatus.OK

                val after = findSession(rawToken)
                after.shouldNotBeNull()
                after.lastSeenAt shouldBe stored.lastSeenAt
                after.idleExpiresAt shouldBe stored.idleExpiresAt
            }

            @Test
            fun `200 - should not slide idle expiry past absolute expiry`() {
                val now = Instant.now()
                val user = persistUser()
                val absoluteExpiresAt = now.plus(Duration.ofHours(1))
                val (rawToken, _) = persistSession(
                    user,
                    lastSeenAt = now.minus(Duration.ofMinutes(2)),
                    idleExpiresAt = now.plus(Duration.ofDays(7)),
                    absoluteExpiresAt = absoluteExpiresAt,
                )

                val response = usersAPIClient.me(user = userWithCookie(user, rawToken))
                response.status shouldBe HttpStatus.OK

                val after = findSession(rawToken)
                after.shouldNotBeNull()
                after.idleExpiresAt shouldBe after.absoluteExpiresAt
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

                val clearedCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
                clearedCookie.shouldNotBeNull()
                clearedCookie.maxAge.seconds shouldBe 0
                findSession(rawToken).shouldBeNull()
            }

            @Test
            fun `401 - should fail when the user was deleted but the session remains`() {
                val user = persistUser()
                val (rawToken, _) = persistSession(user)

                userRepository.deleteById(user.id!!).block()

                val response = usersAPIClient.me(user = userWithCookie(user, rawToken))
                response.status shouldBe HttpStatus.UNAUTHORIZED

                val clearedCookie = response.cookies[sessionProperties.cookieName]?.firstOrNull()
                clearedCookie.shouldNotBeNull()
                clearedCookie.maxAge.seconds shouldBe 0
            }

            @Test
            fun `401 - should fail for a blank AUTH-TOKEN cookie`() {
                persistUser()

                webClient
                    .get()
                    .uri("/api/v1/users/me")
                    .header(HttpHeaders.COOKIE, "${sessionProperties.cookieName}=")
                    .exchange()
                    .expectStatus().isUnauthorized

                webClient
                    .get()
                    .uri("/api/v1/users/me")
                    .header(HttpHeaders.COOKIE, "${sessionProperties.cookieName}=   ")
                    .exchange()
                    .expectStatus().isUnauthorized
            }

            @Test
            fun `401 - should ignore Authorization Bearer without the cookie`() {
                val user = persistUser()
                val (rawToken, _) = persistSession(user)

                webClient
                    .get()
                    .uri("/api/v1/users/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $rawToken")
                    .exchange()
                    .expectStatus().isUnauthorized

                findSession(rawToken).shouldNotBeNull()
            }

            @Test
            fun `401 - should reject a leftover JWT string in AUTH-TOKEN`() {
                persistUser()

                webClient
                    .get()
                    .uri("/api/v1/users/me")
                    .cookie(sessionProperties.cookieName, "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.sig")
                    .exchange()
                    .expectStatus().isUnauthorized
            }
        }
    }

    private fun userWithCookie(user: UserEntity, rawToken: String) =
        MockedAuthenticatedUser(
            token = rawToken,
            userInfo = user.toDTO(),
            authCookie = sessionCookie(rawToken),
            email = user.email
        )
}
