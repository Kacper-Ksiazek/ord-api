package com.ord.controllers.conversations

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
import com.ord.features.conversation.models.conversation.ConversationDTO
import com.ord.features.conversation.models.conversation_message.enums.ConversationMessageSender
import com.ord.features.conversation.models.conversation.enums.ConversationTone
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.repositories.ConversationMessageRepository
import com.ord.features.conversation.repositories.ConversationRepository
import com.ord.testing_utils.api.clients.ConversationAPIClient
import com.ord.testing_utils.api.clients.OngoingConversationAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@DisplayName("- OngoingConversationController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000")
class TestOngoingConversationController @Autowired constructor(
    private val conversationRepository: ConversationRepository,
    private val conversationMessageRepository: ConversationMessageRepository,
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
    private val ongoingConversationAPIClient = OngoingConversationAPIClient(webClient)
    private val conversationAPIClient = ConversationAPIClient(webClient)

    object TestData {
        const val TOPIC = "Discussing weekend plans"
        val LANGUAGE = LanguageName.ENGLISH
        val TYPE = ConversationType.SMALL_TALK
        val TONE = ConversationTone.FRIENDLY
        const val USER_MESSAGE = "I went to the park yesterday and it was beautiful."
        const val AI_MESSAGE = "That sounds lovely! What did you do there?"
    }

    private fun createConversation(user: MockedAuthenticatedUser): ConversationDTO {
        val request = CreateConversationRequest(
            topic = TestData.TOPIC,
            additionalContext = null,
            language = TestData.LANGUAGE,
            tone = TestData.TONE,
            type = TestData.TYPE
        )

        return conversationAPIClient.createConversation(
            body = request,
            user = user
        ).body!!
    }

    private fun saveUserMessage(
        conversationId: UUID,
        content: String,
        messageOrder: Int
    ): UUID {
        val messageId = UUID.randomUUID()

        databaseClient.sql(
            """
            INSERT INTO conversation_messages (id, content, message_order, sender, conversation_id, created_at)
            VALUES (:id, :content, :messageOrder, :sender, :conversationId, :createdAt)
        """
        )
            .bind("id", messageId)
            .bind("content", content)
            .bind("messageOrder", messageOrder)
            .bind("sender", ConversationMessageSender.USER.name)
            .bind("conversationId", conversationId)
            .bind("createdAt", java.time.Instant.now())
            .fetch()
            .rowsUpdated()
            .block()

        return messageId
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/ongoing/initialize-by-ai - initialize conversation with AI message")
    inner class InitializeConversationByAITests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should initialize conversation with additional context`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = conversationAPIClient.createConversation(
                    body = CreateConversationRequest(
                        topic = "Climate change impacts",
                        additionalContext = "Focus on renewable energy solutions",
                        language = TestData.LANGUAGE,
                        tone = TestData.TONE,
                        type = TestData.TYPE
                    ),
                    user = authenticatedUser
                ).body!!

                val response = ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.shouldNotBeBlank()

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "CONVERSATION_INITIALIZE")
            }

            @Test
            fun `200 - should initialize conversation without additional context`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.A2)
                )

                val conversation = conversationAPIClient.createConversation(
                    body = CreateConversationRequest(
                        topic = "Daily routines",
                        additionalContext = null,
                        language = TestData.LANGUAGE,
                        tone = TestData.TONE,
                        type = TestData.TYPE
                    ),
                    user = authenticatedUser
                ).body!!

                val response = ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.shouldNotBeBlank()
            }

            @Test
            fun `200 - initialized message should be persisted`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.A2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val updatedConversation = conversationAPIClient.getConversationById(
                    conversationId = conversation.id,
                    user = authenticatedUser
                ).body!!

                updatedConversation.messages shouldHaveSize 1
                updatedConversation.messages[0].sender shouldBe ConversationMessageSender.AI
                updatedConversation.messages[0].content.shouldNotBeBlank()
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot initialize conversation`() {
                val response = ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = UUID.randomUUID(),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - cannot initialize non-existent conversation`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = UUID.randomUUID(),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - user cannot initialize another user's conversation`() {
                val owner = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )
                val otherUser = mockAuthenticatedUser()

                val conversation = createConversation(owner)

                val response = ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = otherUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `400 - cannot initialize already initialized conversation`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val response = ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/ongoing/request-ai-message - request AI response")
    inner class RequestAIMessageTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should generate AI response to user message`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val request = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    latestMessageId = UUID.randomUUID(),
                    messageOrder = 2,
                    latestUserMessage = TestData.USER_MESSAGE
                )

                val response = ongoingConversationAPIClient.requestAIMessage(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.shouldNotBeBlank()

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "CONVERSATION_AI_RESPONSE")
            }

            @Test
            fun `200 - both user and AI messages should be persisted`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C1)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val request = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    latestMessageId = UUID.randomUUID(),
                    messageOrder = 2,
                    latestUserMessage = TestData.USER_MESSAGE
                )

                ongoingConversationAPIClient.requestAIMessage(
                    body = request,
                    user = authenticatedUser
                )

                val updatedConversation = conversationAPIClient.getConversationById(
                    conversationId = conversation.id,
                    user = authenticatedUser
                ).body!!

                updatedConversation.messages shouldHaveSize 3
                // User message should be saved
                updatedConversation.messages[1].sender shouldBe ConversationMessageSender.USER
                updatedConversation.messages[1].messageOrder shouldBe 1
                updatedConversation.messages[1].content shouldBe TestData.USER_MESSAGE
                // AI message should be saved
                updatedConversation.messages[2].sender shouldBe ConversationMessageSender.AI
                updatedConversation.messages[2].messageOrder shouldBe 2
                updatedConversation.messages[2].content.shouldNotBeBlank()
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot request AI message`() {
                val request = CreateAIConversationMessageRequest(
                    conversationId = UUID.randomUUID(),
                    latestMessageId = UUID.randomUUID(),
                    messageOrder = 2,
                    latestUserMessage = TestData.USER_MESSAGE
                )

                val response = ongoingConversationAPIClient.requestAIMessage(
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - cannot request AI message for non-existent conversation`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = CreateAIConversationMessageRequest(
                    conversationId = UUID.randomUUID(),
                    latestMessageId = UUID.randomUUID(),
                    messageOrder = 2,
                    latestUserMessage = TestData.USER_MESSAGE
                )

                val response = ongoingConversationAPIClient.requestAIMessage(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - user cannot request AI message for another user's conversation`() {
                val owner = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )
                val otherUser = mockAuthenticatedUser()

                val conversation = createConversation(owner)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = owner
                )

                val request = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    latestMessageId = UUID.randomUUID(),
                    messageOrder = 2,
                    latestUserMessage = TestData.USER_MESSAGE
                )

                val response = ongoingConversationAPIClient.requestAIMessage(
                    body = request,
                    user = otherUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/ongoing/generate-feedback - generate feedback for user message")
    inner class GenerateFeedbackTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should generate feedback for user message`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                val aiInitResponse = ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val messageId =
                    saveUserMessage(
                        conversationId = conversation.id,
                        content = TestData.USER_MESSAGE,
                        messageOrder = 1
                    )

                val response = ongoingConversationAPIClient.generateFeedback(
                    user = authenticatedUser,
                    body = ReviewUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = messageId,
                        message = TestData.USER_MESSAGE,
                        messageOrder = 1,
                        latestAIMessage = aiInitResponse.body
                    )
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.grammar shouldBeInRange 0..10
                response.body.vocabulary shouldBeInRange 0..10
                response.body.answerLength shouldBeInRange 0..10

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "CONVERSATION_REVIEW_USER_MESSAGE")
            }

            @Test
            fun `200 - should provide feedback scores`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.A2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val userMessage = "I went shopping yesterday and bought some clothes."
                val messageId = saveUserMessage(
                    conversationId = conversation.id,
                    content = userMessage,
                    messageOrder = 1
                )

                val request = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId,
                    message = userMessage,
                    messageOrder = 1,
                    latestAIMessage = TestData.AI_MESSAGE
                )

                val response = ongoingConversationAPIClient.generateFeedback(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                val feedback = response.body!!
                feedback.grammar shouldBeGreaterThan 0
                feedback.vocabulary shouldBeGreaterThan 0
                feedback.answerLength shouldBeGreaterThan 0
                feedback.naturalness shouldBeInRange 0..10
                feedback.coherenceWithContext shouldBeInRange 0..10
                feedback.registerAppropriate shouldNotBe null
                feedback.mistakes shouldNotBe null
                feedback.strengthsIdentified shouldNotBe null
            }

            @Test
            fun `200 - feedback should be persisted and linked to user message`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C1)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val messageId = saveUserMessage(
                    conversationId = conversation.id,
                    content = TestData.USER_MESSAGE,
                    messageOrder = 1
                )

                val request = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId,
                    message = TestData.USER_MESSAGE,
                    messageOrder = 1,
                    latestAIMessage = TestData.AI_MESSAGE
                )

                ongoingConversationAPIClient.generateFeedback(
                    body = request,
                    user = authenticatedUser
                )

                val updatedConversation = conversationAPIClient.getConversationById(
                    conversationId = conversation.id,
                    user = authenticatedUser
                ).body!!

                updatedConversation.messages shouldHaveSize 2
                updatedConversation.messages[1].sender shouldBe ConversationMessageSender.USER
                updatedConversation.messages[1].messageOrder shouldBe 1
                updatedConversation.messages[1].content shouldBe TestData.USER_MESSAGE
                updatedConversation.messages[1].feedback shouldNotBe null
                updatedConversation.messages[1].feedback!!.grammar shouldBeInRange 0..10
            }

            @Test
            fun `200 - should work without latest AI message`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B1)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val messageId = saveUserMessage(
                    conversationId = conversation.id,
                    content = TestData.USER_MESSAGE,
                    messageOrder = 1
                )

                val request = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId,
                    message = TestData.USER_MESSAGE,
                    messageOrder = 1,
                    latestAIMessage = null
                )

                val response = ongoingConversationAPIClient.generateFeedback(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
            }

            @Test
            fun `200 - should handle multiple user messages`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val message1 = "I like sports."
                val messageId1 = saveUserMessage(
                    conversationId = conversation.id,
                    content = message1,
                    messageOrder = 1
                )

                val message2 = "My favorite is tennis."
                val messageId2 = saveUserMessage(
                    conversationId = conversation.id,
                    content = message2,
                    messageOrder = 3
                )

                val request1 = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId1,
                    message = message1,
                    messageOrder = 1,
                    latestAIMessage = TestData.AI_MESSAGE
                )

                val request2 = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId2,
                    message = message2,
                    messageOrder = 3,
                    latestAIMessage = "What kind of sports do you enjoy?"
                )

                val response1 = ongoingConversationAPIClient.generateFeedback(
                    body = request1,
                    user = authenticatedUser
                )

                val response2 = ongoingConversationAPIClient.generateFeedback(
                    body = request2,
                    user = authenticatedUser
                )

                response1.status shouldBe HttpStatus.OK
                response2.status shouldBe HttpStatus.OK

                val updatedConversation = conversationAPIClient.getConversationById(
                    conversationId = conversation.id,
                    user = authenticatedUser
                ).body!!

                updatedConversation.messages.filter { it.sender == ConversationMessageSender.USER } shouldHaveSize 2
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot generate feedback`() {
                val request = ReviewUserConversationMessageRequest(
                    conversationId = UUID.randomUUID(),
                    messageId = UUID.randomUUID(),
                    message = TestData.USER_MESSAGE,
                    messageOrder = 1,
                    latestAIMessage = TestData.AI_MESSAGE
                )

                val response = ongoingConversationAPIClient.generateFeedback(
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - cannot generate feedback for non-existent conversation`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = ReviewUserConversationMessageRequest(
                    conversationId = UUID.randomUUID(),
                    messageId = UUID.randomUUID(),
                    message = TestData.USER_MESSAGE,
                    messageOrder = 1,
                    latestAIMessage = TestData.AI_MESSAGE
                )

                val response = ongoingConversationAPIClient.generateFeedback(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - user cannot generate feedback for another user's conversation`() {
                val owner = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )
                val otherUser = mockAuthenticatedUser()

                val conversation = createConversation(owner)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = owner
                )

                val request = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = UUID.randomUUID(),
                    message = TestData.USER_MESSAGE,
                    messageOrder = 1,
                    latestAIMessage = TestData.AI_MESSAGE
                )

                val response = ongoingConversationAPIClient.generateFeedback(
                    body = request,
                    user = otherUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `200 - extremely short answers should be detected as sabotage`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val shortMessage = "ok"
                val messageId = saveUserMessage(
                    conversationId = conversation.id,
                    content = shortMessage,
                    messageOrder = 1
                )

                val request = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId,
                    message = shortMessage,
                    messageOrder = 1,
                    latestAIMessage = TestData.AI_MESSAGE
                )

                val response = ongoingConversationAPIClient.generateFeedback(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.sabotage shouldNotBe null
            }
        }
    }

    @Nested
    @DisplayName("Integration - Full conversation flow")
    inner class FullConversationFlowTests {

        @Test
        fun `should complete full conversation cycle`() {
            val authenticatedUser = mockAuthenticatedUser(
                languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
            )

            val conversation = createConversation(authenticatedUser)

            val aiInit = ongoingConversationAPIClient.initializeConversationByAI(
                conversationId = conversation.id,
                user = authenticatedUser
            )
            aiInit.status shouldBe HttpStatus.OK

            // First user message - save via requestAIMessage and get feedback
            val userMessageId1 = UUID.randomUUID()
            val userMessageContent1 = "I went to the beach last weekend."
            val aiMessage1 = ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    latestMessageId = userMessageId1,
                    messageOrder = 2,  // AI message order
                    latestUserMessage = userMessageContent1
                ),
                user = authenticatedUser
            )
            aiMessage1.status shouldBe HttpStatus.OK

            val userFeedback1 = ongoingConversationAPIClient.generateFeedback(
                body = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId1,
                    message = userMessageContent1,
                    messageOrder = 1,
                    latestAIMessage = aiInit.body
                ),
                user = authenticatedUser
            )
            userFeedback1.status shouldBe HttpStatus.OK

            // Second user message - save via requestAIMessage and get feedback
            val userMessageId2 = UUID.randomUUID()
            val userMessageContent2 = "Yes, it was very relaxing and sunny."
            val aiMessage2 = ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    latestMessageId = userMessageId2,
                    messageOrder = 4,  // AI message order
                    latestUserMessage = userMessageContent2
                ),
                user = authenticatedUser
            )
            aiMessage2.status shouldBe HttpStatus.OK

            val userFeedback2 = ongoingConversationAPIClient.generateFeedback(
                body = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId2,
                    message = userMessageContent2,
                    messageOrder = 3,
                    latestAIMessage = aiMessage1.body
                ),
                user = authenticatedUser
            )
            userFeedback2.status shouldBe HttpStatus.OK

            val finalConversation = conversationAPIClient.getConversationById(
                conversationId = conversation.id,
                user = authenticatedUser
            ).body!!

            finalConversation.messages shouldHaveSize 5
            finalConversation.messages[0].sender shouldBe ConversationMessageSender.AI
            finalConversation.messages[1].sender shouldBe ConversationMessageSender.USER
            finalConversation.messages[2].sender shouldBe ConversationMessageSender.AI
            finalConversation.messages[3].sender shouldBe ConversationMessageSender.USER
            finalConversation.messages[4].sender shouldBe ConversationMessageSender.AI

            finalConversation.messages[1].feedback shouldNotBe null
            finalConversation.messages[3].feedback shouldNotBe null
        }

        @Test
        fun `should maintain message order throughout conversation`() {
            val authenticatedUser = mockAuthenticatedUser(
                languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C1)
            )

            val conversation = createConversation(authenticatedUser)

            ongoingConversationAPIClient.initializeConversationByAI(
                conversationId = conversation.id,
                user = authenticatedUser
            )

            val userMessageId1 = UUID.randomUUID()
            ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    latestMessageId = userMessageId1,
                    messageOrder = 2,
                    latestUserMessage = "First message"
                ),
                user = authenticatedUser
            )

            ongoingConversationAPIClient.generateFeedback(
                body = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId1,
                    message = "First message",
                    messageOrder = 1
                ),
                user = authenticatedUser
            )

            val userMessageId2 = UUID.randomUUID()
            ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    latestMessageId = userMessageId2,
                    messageOrder = 4,
                    latestUserMessage = "Second message"
                ),
                user = authenticatedUser
            )

            ongoingConversationAPIClient.generateFeedback(
                body = ReviewUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId2,
                    message = "Second message",
                    messageOrder = 3
                ),
                user = authenticatedUser
            )

            val finalConversation = conversationAPIClient.getConversationById(
                conversationId = conversation.id,
                user = authenticatedUser
            ).body!!

            finalConversation.messages[0].messageOrder shouldBe 0
            finalConversation.messages[1].messageOrder shouldBe 1
            finalConversation.messages[2].messageOrder shouldBe 2
            finalConversation.messages[3].messageOrder shouldBe 3
            finalConversation.messages[4].messageOrder shouldBe 4
        }
    }
}
