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
import com.ord.features.conversation.api.requests.GetFeedbackOnUserConversationMessageRequest
import com.ord.features.conversation.api.requests.GetLearningTipsForAIMessageRequest
import com.ord.features.conversation.api.requests.SaveUserConversationMessageRequest
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
        const val AI_INTERLOCUTOR_NAME = "Dr. Smith"
        const val AI_INTERLOCUTOR_AVATAR_ID = "AVATAR_ALPHA"
        const val USER_MESSAGE = "I went to the park yesterday and it was absolutely beautiful. The weather was perfect - sunny with a light breeze. I spent several hours walking around the lake, watching the ducks and enjoying the peaceful atmosphere. There were families having picnics on the grass, children playing on the swings, and joggers running along the paths. I brought a book with me and found a nice bench under a big oak tree where I sat and read for a while. It was such a relaxing and enjoyable afternoon that I'm definitely planning to go back there again this weekend."
        const val AI_MESSAGE = "That sounds lovely! What did you do there?"
    }

    private fun createConversation(user: MockedAuthenticatedUser): ConversationDTO {
        val request = CreateConversationRequest(
            topic = TestData.TOPIC,
            additionalContext = null,
            language = TestData.LANGUAGE,
            tone = TestData.TONE,
            type = TestData.TYPE,
            aiInterlocutorName = TestData.AI_INTERLOCUTOR_NAME,
            aiInterlocutorAvatarId = TestData.AI_INTERLOCUTOR_AVATAR_ID
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

    private fun saveAIMessage(
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
            .bind("sender", ConversationMessageSender.AI.name)
            .bind("conversationId", conversationId)
            .bind("createdAt", java.time.Instant.now())
            .fetch()
            .rowsUpdated()
            .block()

        return messageId
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/ongoing/ai/initialize - initialize conversation with AI message")
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
                        type = TestData.TYPE,
                        aiInterlocutorName = TestData.AI_INTERLOCUTOR_NAME,
                        aiInterlocutorAvatarId = TestData.AI_INTERLOCUTOR_AVATAR_ID
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
                        type = TestData.TYPE,
                        aiInterlocutorName = TestData.AI_INTERLOCUTOR_NAME,
                        aiInterlocutorAvatarId = TestData.AI_INTERLOCUTOR_AVATAR_ID
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
    @DisplayName("[POST] /api/v1/conversations/ongoing/ai/request-message - request AI response")
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
            fun `200 - AI message should be persisted`() {
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

                // Only AI message should be saved (user message is NOT saved by /request-ai-message)
                updatedConversation.messages shouldHaveSize 2
                updatedConversation.messages[0].sender shouldBe ConversationMessageSender.AI
                updatedConversation.messages[1].sender shouldBe ConversationMessageSender.AI
                updatedConversation.messages[1].messageOrder shouldBe 2
                updatedConversation.messages[1].content.shouldNotBeBlank()
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot request AI message`() {
                val request = CreateAIConversationMessageRequest(
                    conversationId = UUID.randomUUID(),
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
    @DisplayName("[POST] /api/v1/conversations/ongoing/user/save-message - save user message")
    inner class SaveUserMessageTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should save user message and return DTO`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)
                val messageId = UUID.randomUUID()
                val messageContent = "This is my first message."

                val response = ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = messageId,
                        content = messageContent,
                        messageOrder = 1
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.id shouldBe messageId
                response.body.content shouldBe messageContent
                response.body.messageOrder shouldBe 1
                response.body.sender shouldBe ConversationMessageSender.USER
            }

            @Test
            fun `200 - saved message should be persisted in database`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C1)
                )

                val conversation = createConversation(authenticatedUser)
                val messageId = UUID.randomUUID()
                val messageContent = "Hello, how are you?"

                ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = messageId,
                        content = messageContent,
                        messageOrder = 0
                    ),
                    user = authenticatedUser
                )

                val updatedConversation = conversationAPIClient.getConversationById(
                    conversationId = conversation.id,
                    user = authenticatedUser
                ).body!!

                updatedConversation.messages shouldHaveSize 1
                updatedConversation.messages[0].id shouldBe messageId
                updatedConversation.messages[0].content shouldBe messageContent
                updatedConversation.messages[0].sender shouldBe ConversationMessageSender.USER
            }

            @Test
            fun `200 - should save multiple messages with correct order`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.A2)
                )

                val conversation = createConversation(authenticatedUser)

                val message1Id = UUID.randomUUID()
                val message1Content = "First message"
                ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = message1Id,
                        content = message1Content,
                        messageOrder = 0
                    ),
                    user = authenticatedUser
                )

                val message2Id = UUID.randomUUID()
                val message2Content = "Second message"
                ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = message2Id,
                        content = message2Content,
                        messageOrder = 2
                    ),
                    user = authenticatedUser
                )

                val updatedConversation = conversationAPIClient.getConversationById(
                    conversationId = conversation.id,
                    user = authenticatedUser
                ).body!!

                updatedConversation.messages shouldHaveSize 2
                updatedConversation.messages[0].messageOrder shouldBe 0
                updatedConversation.messages[0].content shouldBe message1Content
                updatedConversation.messages[1].messageOrder shouldBe 2
                updatedConversation.messages[1].content shouldBe message2Content
            }

            @Test
            fun `200 - should save message with frontend-generated UUID`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B1)
                )

                val conversation = createConversation(authenticatedUser)
                val customMessageId = UUID.fromString("12345678-1234-1234-1234-123456789012")

                val response = ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = customMessageId,
                        content = "This is a test message with a custom ID that I'm using to verify the conversation message saving functionality. I want to make sure that the system properly handles messages with pre-assigned IDs and stores them correctly in the database. This message is intentionally longer to avoid any issues with message length validation or sabotage detection mechanisms.",
                        messageOrder = 0
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body!!.id shouldBe customMessageId
            }

            @Test
            fun `200 - returned DTO should contain all required fields`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)
                val messageId = UUID.randomUUID()

                val response = ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = messageId,
                        content = "This is a comprehensive test message that I'm writing to thoroughly test the conversation message saving functionality. I want to ensure that the system properly handles longer messages with appropriate content, maintains the correct message order, and validates all the required fields correctly. This detailed message should help verify that the message persistence layer works as expected.",
                        messageOrder = 5
                    ),
                    user = authenticatedUser
                )

                val dto = response.body!!
                dto.id shouldNotBe null
                dto.content shouldNotBe null
                dto.messageOrder shouldNotBe null
                dto.sender shouldNotBe null
                dto.createdAt shouldNotBe null
            }

            @Test
            fun `200 - should handle long message content`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C2)
                )

                val conversation = createConversation(authenticatedUser)
                val messageId = UUID.randomUUID()
                val longMessage = "Lorem ipsum dolor sit amet. ".repeat(50) // ~1400 chars

                val response = ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = messageId,
                        content = longMessage,
                        messageOrder = 0
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body!!.content shouldBe longMessage
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot save message`() {
                val response = ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = UUID.randomUUID(),
                        messageId = UUID.randomUUID(),
                        content = "This is a test message for verifying authentication and authorization mechanisms in the conversation system. I'm deliberately testing what happens when an unauthorized user attempts to save a message to a conversation. This longer message ensures we're not triggering any sabotage detection while properly testing the security boundaries of our application.",
                        messageOrder = 0
                    ),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - cannot save message to non-existent conversation`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = UUID.randomUUID(),
                        messageId = UUID.randomUUID(),
                        content = "This is a test message designed to verify the system's behavior when attempting to save a message to a conversation that doesn't exist in the database. I want to ensure that the application correctly handles this edge case and returns the appropriate 404 status code. This comprehensive message also helps avoid triggering any sabotage detection mechanisms that might interfere with the test.",
                        messageOrder = 0
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - user cannot save message to another user's conversation`() {
                val owner = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )
                val otherUser = mockAuthenticatedUser()

                val conversation = createConversation(owner)

                val response = ongoingConversationAPIClient.saveUserMessage(
                    body = SaveUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = UUID.randomUUID(),
                        content = "This is a test message being sent by an unauthorized user who should not have access to this conversation. I'm verifying that the system properly enforces ownership and access control rules to prevent users from saving messages to conversations they don't own. This comprehensive test message ensures we're properly testing security boundaries while avoiding any sabotage detection issues.",
                        messageOrder = 0
                    ),
                    user = otherUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/ongoing/user/generate-feedback - generate feedback for user message")
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
                    body = GetFeedbackOnUserConversationMessageRequest(
                        conversationId = conversation.id,
                        messageId = messageId,
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

                val request = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId,
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

                val request = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId,
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

                val request = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId,
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

                val request1 = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId1,
                    messageOrder = 1,
                    latestAIMessage = TestData.AI_MESSAGE
                )

                val request2 = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId2,
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
                val request = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = UUID.randomUUID(),
                    messageId = UUID.randomUUID(),
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

                val request = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = UUID.randomUUID(),
                    messageId = UUID.randomUUID(),
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

                // Owner saves a message
                val messageId = saveUserMessage(
                    conversationId = conversation.id,
                    content = TestData.USER_MESSAGE,
                    messageOrder = 1
                )

                // Other user tries to generate feedback for owner's message
                val request = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = messageId,
                    messageOrder = 1,
                    latestAIMessage = TestData.AI_MESSAGE
                )

                val response = ongoingConversationAPIClient.generateFeedback(
                    body = request,
                    user = otherUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/ongoing/ai/generate-learning-tips - generate learning tips for AI message")
    inner class GenerateLearningTipsForAIMessageTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should generate learning tips for latest AI message`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val aiMessageId = saveAIMessage(
                    conversationId = conversation.id,
                    content = "That sounds wonderful! Could you describe the scenery in more detail? What makes this place so special to you?",
                    messageOrder = 1
                )

                val response = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.grammarTips shouldNotBe null
                response.body.vocabularyTips shouldNotBe null
                response.body.idiomTips shouldNotBe null

                // Verify the tips were added to the latest AI message
                val updatedConversation = conversationAPIClient.getConversationById(
                    conversationId = conversation.id,
                    user = authenticatedUser
                ).body!!

                val tippedMessage = updatedConversation.messages.find { it.id == aiMessageId }
                tippedMessage shouldNotBe null
                tippedMessage!!.learningTips shouldNotBe null

                assertGptTokensLogCreated(authenticatedUser.userInfo.id, "CONVERSATION_GENERATE_AI_MESSAGE_LEARNING_TIPS")
            }

            @Test
            fun `200 - should return all tip categories with proper structure`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C1)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                saveAIMessage(
                    conversationId = conversation.id,
                    content = "It's fascinating how you've described the atmosphere. The way you mentioned the oak tree and the peaceful surroundings really paints a vivid picture. Have you considered visiting other parks in the area?",
                    messageOrder = 1
                )

                val response = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                val tips = response.body!!

                tips.grammarTips shouldNotBe null
                tips.vocabularyTips shouldNotBe null
                tips.idiomTips shouldNotBe null
            }

            @Test
            fun `200 - learning tips should be persisted and linked to message`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val aiMessageId = saveAIMessage(
                    conversationId = conversation.id,
                    content = "That's wonderful! I'd love to hear more about your experience. What was your favorite part of the visit?",
                    messageOrder = 1
                )

                ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                val updatedConversation = conversationAPIClient.getConversationById(
                    conversationId = conversation.id,
                    user = authenticatedUser
                ).body!!

                val aiMessage = updatedConversation.messages.find { it.id == aiMessageId }
                aiMessage shouldNotBe null
                aiMessage!!.learningTips shouldNotBe null
            }

            @Test
            fun `200 - should work with different language proficiency levels`() {
                val proficiencyLevels = listOf(
                    LanguageProficiencyLevel.A2,
                    LanguageProficiencyLevel.B2,
                    LanguageProficiencyLevel.C1
                )

                proficiencyLevels.forEach { level ->
                    val authenticatedUser = mockAuthenticatedUser(
                        languages = mapOf(TestData.LANGUAGE to level)
                    )

                    val conversation = createConversation(authenticatedUser)

                    ongoingConversationAPIClient.initializeConversationByAI(
                        conversationId = conversation.id,
                        user = authenticatedUser
                    )

                    saveAIMessage(
                        conversationId = conversation.id,
                        content = "That sounds lovely! What did you enjoy most?",
                        messageOrder = 1
                    )

                    val response = ongoingConversationAPIClient.generateLearningTips(
                        body = GetLearningTipsForAIMessageRequest(
                            conversationId = conversation.id
                        ),
                        user = authenticatedUser
                    )

                    response.status shouldBe HttpStatus.OK
                    response.body shouldNotBe null
                }
            }

            @Test
            fun `200 - returned DTO should contain all required fields`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                saveAIMessage(
                    conversationId = conversation.id,
                    content = "That's great! The park sounds like a perfect place to relax and enjoy nature.",
                    messageOrder = 1
                )

                val response = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                val dto = response.body!!
                dto.grammarTips shouldNotBe null
                dto.vocabularyTips shouldNotBe null
                dto.idiomTips shouldNotBe null
            }

            @Test
            fun `200 - should handle AI messages with various content lengths`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C2)
                )

                val conversation = createConversation(authenticatedUser)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                val shortMessage = saveAIMessage(
                    conversationId = conversation.id,
                    content = "That's nice!",
                    messageOrder = 1
                )

                val longMessage = saveAIMessage(
                    conversationId = conversation.id,
                    content = "It's absolutely wonderful to hear about your park experience! The way you've described the serene atmosphere, with the ducks gliding across the lake and families enjoying their time together, really brings the scene to life. I can almost picture the majestic oak tree providing shade as you sat there immersed in your book. Nature has such a remarkable way of rejuvenating our spirits, doesn't it?",
                    messageOrder = 3
                )

                // Generate tips for long message first (it's the latest)
                val longResponse = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                // Generate tips for short message (now it's the latest without tips)
                val shortResponse = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                shortResponse.status shouldBe HttpStatus.OK
                shortResponse.body shouldNotBe null
                longResponse.status shouldBe HttpStatus.OK
                longResponse.body shouldNotBe null
            }

            @Test
            fun `200 - should generate tips for latest AI message when multiple untipped messages exist`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                // Initialize - creates AI message at order 0
                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                // Add another AI message at order 1
                val olderMessageId = saveAIMessage(
                    conversationId = conversation.id,
                    content = "This is an older message",
                    messageOrder = 1
                )

                // Add latest AI message at order 2
                val latestMessageId = saveAIMessage(
                    conversationId = conversation.id,
                    content = "This is the latest message",
                    messageOrder = 2
                )

                // Generate tips - should target latest message (order 2)
                val response = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify tips were added to latest message only
                val updatedConversation = conversationAPIClient.getConversationById(
                    conversationId = conversation.id,
                    user = authenticatedUser
                ).body!!

                val latestMessage = updatedConversation.messages.find { it.id == latestMessageId }
                val olderMessage = updatedConversation.messages.find { it.id == olderMessageId }

                latestMessage shouldNotBe null
                latestMessage!!.learningTips shouldNotBe null
                olderMessage shouldNotBe null
                olderMessage!!.learningTips shouldBe null  // Older message should not have tips
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot generate learning tips`() {
                val response = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = UUID.randomUUID()
                    ),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - cannot generate learning tips for non-existent conversation`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = UUID.randomUUID()
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - user cannot generate learning tips for another user's conversation`() {
                val owner = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )
                val otherUser = mockAuthenticatedUser()

                val conversation = createConversation(owner)

                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = owner
                )

                saveAIMessage(
                    conversationId = conversation.id,
                    content = "That sounds wonderful!",
                    messageOrder = 1
                )

                val response = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = otherUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - cannot generate learning tips when no AI messages exist in conversation`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                // Create conversation but don't initialize (no AI messages)
                val conversation = createConversation(authenticatedUser)

                val response = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - cannot generate learning tips when all AI messages already have tips`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation = createConversation(authenticatedUser)

                // Initialize conversation - creates first AI message
                ongoingConversationAPIClient.initializeConversationByAI(
                    conversationId = conversation.id,
                    user = authenticatedUser
                )

                // Generate learning tips for the AI message
                ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                // Try to generate tips again - should fail (all messages have tips)
                val response = ongoingConversationAPIClient.generateLearningTips(
                    body = GetLearningTipsForAIMessageRequest(
                        conversationId = conversation.id
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
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

            // First user message - save, get AI response, and get feedback
            val userMessageId1 = UUID.randomUUID()
            val userMessageContent1 = "I went to the beach last weekend."

            ongoingConversationAPIClient.saveUserMessage(
                body = SaveUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId1,
                    content = userMessageContent1,
                    messageOrder = 1
                ),
                user = authenticatedUser
            )

            val aiMessage1 = ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    messageOrder = 2,  // AI message order
                    latestUserMessage = userMessageContent1
                ),
                user = authenticatedUser
            )
            aiMessage1.status shouldBe HttpStatus.OK

            val userFeedback1 = ongoingConversationAPIClient.generateFeedback(
                body = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId1,
                    messageOrder = 1,
                    latestAIMessage = aiInit.body
                ),
                user = authenticatedUser
            )
            userFeedback1.status shouldBe HttpStatus.OK

            // Second user message - save, get AI response, and get feedback
            val userMessageId2 = UUID.randomUUID()
            val userMessageContent2 = "Yes, it was very relaxing and sunny."

            ongoingConversationAPIClient.saveUserMessage(
                body = SaveUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId2,
                    content = userMessageContent2,
                    messageOrder = 3
                ),
                user = authenticatedUser
            )

            val aiMessage2 = ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    messageOrder = 4,  // AI message order
                    latestUserMessage = userMessageContent2
                ),
                user = authenticatedUser
            )
            aiMessage2.status shouldBe HttpStatus.OK

            val userFeedback2 = ongoingConversationAPIClient.generateFeedback(
                body = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId2,
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
            val firstMessageContent = "This is my first message in this conversation and I wanted to share my thoughts about the topic we're discussing. I've been thinking about this for quite some time now and I have several interesting points to make. Let me start by explaining my perspective on the matter and provide some context that will help you understand where I'm coming from with my analysis."
            ongoingConversationAPIClient.saveUserMessage(
                body = SaveUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId1,
                    content = firstMessageContent,
                    messageOrder = 1
                ),
                user = authenticatedUser
            )

            ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    messageOrder = 2,
                    latestUserMessage = firstMessageContent
                ),
                user = authenticatedUser
            )

            ongoingConversationAPIClient.generateFeedback(
                body = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId1,
                    messageOrder = 1
                ),
                user = authenticatedUser
            )

            val userMessageId2 = UUID.randomUUID()
            val secondMessageContent = "Now I'd like to follow up on my previous message with some additional thoughts and observations. Building on what I mentioned earlier, I want to delve deeper into the specific aspects of this topic that I find particularly interesting. There are several key points that I believe warrant further discussion and I'd like to explore each of them in detail."
            ongoingConversationAPIClient.saveUserMessage(
                body = SaveUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId2,
                    content = secondMessageContent,
                    messageOrder = 3
                ),
                user = authenticatedUser
            )

            ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    messageOrder = 4,
                    latestUserMessage = secondMessageContent
                ),
                user = authenticatedUser
            )

            ongoingConversationAPIClient.generateFeedback(
                body = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId2,
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

        @Test
        fun `should generate both feedback and learning tips in same conversation`() {
            val authenticatedUser = mockAuthenticatedUser(
                languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
            )

            val conversation = createConversation(authenticatedUser)

            val aiInit = ongoingConversationAPIClient.initializeConversationByAI(
                conversationId = conversation.id,
                user = authenticatedUser
            )
            aiInit.status shouldBe HttpStatus.OK

            val userMessageId = UUID.randomUUID()
            val userMessageContent = "I went to the park yesterday and had a wonderful time enjoying the beautiful scenery."

            ongoingConversationAPIClient.saveUserMessage(
                body = SaveUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId,
                    content = userMessageContent,
                    messageOrder = 1
                ),
                user = authenticatedUser
            )

            val aiMessageResponse = ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    messageOrder = 2,
                    latestUserMessage = userMessageContent
                ),
                user = authenticatedUser
            )
            aiMessageResponse.status shouldBe HttpStatus.OK

            val feedbackResponse = ongoingConversationAPIClient.generateFeedback(
                body = GetFeedbackOnUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessageId,
                    messageOrder = 1,
                    latestAIMessage = aiInit.body
                ),
                user = authenticatedUser
            )
            feedbackResponse.status shouldBe HttpStatus.OK

            val finalConversation = conversationAPIClient.getConversationById(
                conversationId = conversation.id,
                user = authenticatedUser
            ).body!!

            val aiMessageId = finalConversation.messages.find { it.messageOrder == 2 }!!.id

            val learningTipsResponse = ongoingConversationAPIClient.generateLearningTips(
                body = GetLearningTipsForAIMessageRequest(
                    conversationId = conversation.id
                ),
                user = authenticatedUser
            )
            learningTipsResponse.status shouldBe HttpStatus.OK

            val updatedConversation = conversationAPIClient.getConversationById(
                conversationId = conversation.id,
                user = authenticatedUser
            ).body!!

            val userMessage = updatedConversation.messages.find { it.id == userMessageId }!!
            val aiMessage = updatedConversation.messages.find { it.id == aiMessageId }!!

            userMessage.feedback shouldNotBe null
            aiMessage.learningTips shouldNotBe null
        }

        @Test
        fun `should generate learning tips for multiple AI messages`() {
            val authenticatedUser = mockAuthenticatedUser(
                languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C1)
            )

            val conversation = createConversation(authenticatedUser)

            val aiInit = ongoingConversationAPIClient.initializeConversationByAI(
                conversationId = conversation.id,
                user = authenticatedUser
            )
            aiInit.status shouldBe HttpStatus.OK

            val userMessage1Id = UUID.randomUUID()
            val userMessage1Content = "I love reading books about history and science."

            ongoingConversationAPIClient.saveUserMessage(
                body = SaveUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessage1Id,
                    content = userMessage1Content,
                    messageOrder = 1
                ),
                user = authenticatedUser
            )

            val aiMessage1Response = ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    messageOrder = 2,
                    latestUserMessage = userMessage1Content
                ),
                user = authenticatedUser
            )
            aiMessage1Response.status shouldBe HttpStatus.OK

            val conversationAfterFirstExchange = conversationAPIClient.getConversationById(
                conversationId = conversation.id,
                user = authenticatedUser
            ).body!!

            val aiMessage1Id = conversationAfterFirstExchange.messages.find { it.messageOrder == 2 }!!.id

            val learningTips1 = ongoingConversationAPIClient.generateLearningTips(
                body = GetLearningTipsForAIMessageRequest(
                    conversationId = conversation.id
                ),
                user = authenticatedUser
            )
            learningTips1.status shouldBe HttpStatus.OK

            val userMessage2Id = UUID.randomUUID()
            val userMessage2Content = "What topics interest you the most?"

            ongoingConversationAPIClient.saveUserMessage(
                body = SaveUserConversationMessageRequest(
                    conversationId = conversation.id,
                    messageId = userMessage2Id,
                    content = userMessage2Content,
                    messageOrder = 3
                ),
                user = authenticatedUser
            )

            val aiMessage2Response = ongoingConversationAPIClient.requestAIMessage(
                body = CreateAIConversationMessageRequest(
                    conversationId = conversation.id,
                    messageOrder = 4,
                    latestUserMessage = userMessage2Content
                ),
                user = authenticatedUser
            )
            aiMessage2Response.status shouldBe HttpStatus.OK

            val conversationAfterSecondExchange = conversationAPIClient.getConversationById(
                conversationId = conversation.id,
                user = authenticatedUser
            ).body!!

            val aiMessage2Id = conversationAfterSecondExchange.messages.find { it.messageOrder == 4 }!!.id

            val learningTips2 = ongoingConversationAPIClient.generateLearningTips(
                body = GetLearningTipsForAIMessageRequest(
                    conversationId = conversation.id
                ),
                user = authenticatedUser
            )
            learningTips2.status shouldBe HttpStatus.OK

            val finalConversation = conversationAPIClient.getConversationById(
                conversationId = conversation.id,
                user = authenticatedUser
            ).body!!

            val aiMsg1 = finalConversation.messages.find { it.id == aiMessage1Id }!!
            val aiMsg2 = finalConversation.messages.find { it.id == aiMessage2Id }!!

            aiMsg1.learningTips shouldNotBe null
            aiMsg2.learningTips shouldNotBe null
        }
    }
}
