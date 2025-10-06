package com.ord.controllers.conversations

import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.api.requests.GenerateAIInterlocutorDataRequest
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.features.conversation.models.enums.ConversationTone
import com.ord.features.conversation.models.enums.ConversationType
import com.ord.features.conversation.repositories.ConversationRepository
import com.ord.config.properties.JwtProperties
import com.ord.testing_utils.api.clients.ConversationAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@DisplayName("- ConversationController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000")
class TestConversationController @Autowired constructor(
    private val conversationRepository: ConversationRepository,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository
) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository
) {
    private val conversationAPIClient = ConversationAPIClient(webClient)

    object TestData {
        const val TOPIC = "Discussing the weather and climate change"
        const val ADDITIONAL_CONTEXT = "Focus on environmental impact"
        val LANGUAGE = LanguageName.ENGLISH
        val TYPE = ConversationType.SMALL_TALK
        val TONE = ConversationTone.FRIENDLY
        const val AI_INTERLOCUTOR_NAME = "Dr. Smith"
        const val AI_INTERLOCUTOR_AVATAR_ID = "AVATAR_ALPHA"
    }

    @AfterEach
    fun cleanup() {
        conversationRepository.deleteAll().block()
    }

    private fun createConversationEntity(
        userId: UUID,
        topic: String = TestData.TOPIC,
        additionalContext: String? = TestData.ADDITIONAL_CONTEXT,
        language: LanguageName = TestData.LANGUAGE,
        proficiencyLevel: LanguageProficiencyLevel = LanguageProficiencyLevel.B2,
        type: ConversationType = TestData.TYPE,
        tone: ConversationTone = TestData.TONE,
        aiInterlocutorName: String? = TestData.AI_INTERLOCUTOR_NAME,
        aiInterlocutorAvatarId: String? = TestData.AI_INTERLOCUTOR_AVATAR_ID
    ): ConversationEntity {
        return conversationRepository.save(
            ConversationEntity(
                topic = topic,
                additionalContext = additionalContext,
                language = language,
                proficiencyLevel = proficiencyLevel,
                type = type,
                aiTone = tone,
                aiInterlocutorName = aiInterlocutorName,
                aiInterlocutorAvatarId = aiInterlocutorAvatarId,
                userId = userId
            )
        ).block()!!
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/suggest-ai-interlocutor - generate AI interlocutor data")
    inner class GenerateAIInterlocutorTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should generate AI interlocutor data with additional context`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val request = GenerateAIInterlocutorDataRequest(
                    topic = TestData.TOPIC,
                    additionalContext = TestData.ADDITIONAL_CONTEXT,
                    conversationType = TestData.TYPE,
                    language = TestData.LANGUAGE
                )

                val response = conversationAPIClient.generateAIInterlocutor(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.name.shouldNotBeBlank()
                response.body.avatarId shouldNotBe null
            }

            @Test
            fun `200 - should generate AI interlocutor data without additional context`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.A2)
                )

                val request = GenerateAIInterlocutorDataRequest(
                    topic = "Ordering coffee at a café",
                    additionalContext = null,
                    conversationType = ConversationType.SCENARIO_ROLEPLAY,
                    language = TestData.LANGUAGE
                )

                val response = conversationAPIClient.generateAIInterlocutor(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.name.shouldNotBeBlank()
                response.body.avatarId shouldNotBe null
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot generate AI interlocutor`() {
                val request = GenerateAIInterlocutorDataRequest(
                    topic = TestData.TOPIC,
                    additionalContext = null,
                    conversationType = TestData.TYPE,
                    language = TestData.LANGUAGE
                )

                val response = conversationAPIClient.generateAIInterlocutor(
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - topic too short`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B1)
                )

                val request = GenerateAIInterlocutorDataRequest(
                    topic = "",
                    additionalContext = null,
                    conversationType = TestData.TYPE,
                    language = TestData.LANGUAGE
                )

                val response = conversationAPIClient.generateAIInterlocutor(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - topic too long`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B1)
                )

                val request = GenerateAIInterlocutorDataRequest(
                    topic = "x".repeat(501),
                    additionalContext = null,
                    conversationType = TestData.TYPE,
                    language = TestData.LANGUAGE
                )

                val response = conversationAPIClient.generateAIInterlocutor(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - additional context too long`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B1)
                )

                val request = GenerateAIInterlocutorDataRequest(
                    topic = TestData.TOPIC,
                    additionalContext = "x".repeat(1001),
                    conversationType = TestData.TYPE,
                    language = TestData.LANGUAGE
                )

                val response = conversationAPIClient.generateAIInterlocutor(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - user has no proficiency for the language`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.SPANISH to LanguageProficiencyLevel.B1)
                )

                val request = GenerateAIInterlocutorDataRequest(
                    topic = TestData.TOPIC,
                    additionalContext = null,
                    conversationType = TestData.TYPE,
                    language = LanguageName.FRENCH
                )

                val response = conversationAPIClient.generateAIInterlocutor(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[GET] /api/v1/conversations/ - get all conversations for user")
    inner class GetConversationsTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return empty list when user has no conversations`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = conversationAPIClient.getConversations(
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.shouldBeEmpty()
            }

            @Test
            fun `200 - should return user's conversations`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                createConversationEntity(userId = authenticatedUser.userInfo.id)

                val response = conversationAPIClient.getConversations(
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!! shouldHaveSize 1
                response.body[0].topic shouldBe TestData.TOPIC
                response.body[0].type shouldBe TestData.TYPE
            }

            @Test
            fun `200 - should only return conversations belonging to the user`() {
                val user1 = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )
                val user2 = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                createConversationEntity(userId = user1.userInfo.id)
                createConversationEntity(userId = user2.userInfo.id)

                val response1 = conversationAPIClient.getConversations(user = user1)
                val response2 = conversationAPIClient.getConversations(user = user2)

                response1.body!! shouldHaveSize 1
                response2.body!! shouldHaveSize 1
                response1.body[0].id shouldNotBe response2.body[0].id
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot get conversations`() {
                val response = conversationAPIClient.getConversations(user = null)

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }
        }
    }

    @Nested
    @DisplayName("[GET] /api/v1/conversations/{conversationId} - get conversation by ID")
    inner class GetConversationByIdTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should return conversation by ID`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C1)
                )

                val created = createConversationEntity(
                    userId = authenticatedUser.userInfo.id,
                    proficiencyLevel = LanguageProficiencyLevel.C1
                )

                val response = conversationAPIClient.getConversationById(
                    conversationId = created.id!!,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.id shouldBe created.id
                response.body.topic shouldBe TestData.TOPIC
                response.body.additionalContext shouldBe TestData.ADDITIONAL_CONTEXT
                response.body.type shouldBe TestData.TYPE
                response.body.aiTone shouldBe TestData.TONE
                response.body.aiInterlocutorName shouldBe TestData.AI_INTERLOCUTOR_NAME
                response.body.aiInterlocutorAvatarId shouldBe TestData.AI_INTERLOCUTOR_AVATAR_ID
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot get conversation by ID`() {
                val response = conversationAPIClient.getConversationById(
                    conversationId = UUID.randomUUID(),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - should return 404 for non-existent conversation`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = conversationAPIClient.getConversationById(
                    conversationId = UUID.randomUUID(),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - user cannot access another user's conversation`() {
                val owner = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )
                val otherUser = mockAuthenticatedUser()

                val created = createConversationEntity(userId = owner.userInfo.id)

                val response = conversationAPIClient.getConversationById(
                    conversationId = created.id!!,
                    user = otherUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/ - create conversation")
    inner class CreateConversationTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - should create conversation with all fields`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B1)
                )

                val request = CreateConversationRequest(
                    topic = TestData.TOPIC,
                    additionalContext = TestData.ADDITIONAL_CONTEXT,
                    language = TestData.LANGUAGE,
                    tone = TestData.TONE,
                    type = TestData.TYPE,
                    aiInterlocutorName = TestData.AI_INTERLOCUTOR_NAME,
                    aiInterlocutorAvatarId = TestData.AI_INTERLOCUTOR_AVATAR_ID
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.id shouldNotBe null
                response.body.topic shouldBe TestData.TOPIC
                response.body.additionalContext shouldBe TestData.ADDITIONAL_CONTEXT
                response.body.language shouldBe TestData.LANGUAGE
                response.body.type shouldBe TestData.TYPE
                response.body.aiTone shouldBe TestData.TONE
                response.body.aiInterlocutorName shouldBe TestData.AI_INTERLOCUTOR_NAME
                response.body.aiInterlocutorAvatarId shouldBe TestData.AI_INTERLOCUTOR_AVATAR_ID
                response.body.proficiencyLevel shouldBe LanguageProficiencyLevel.B1
                response.body.messages shouldNotBe null
                response.body.messages shouldHaveSize 0
            }

            @Test
            fun `201 - should create conversation without optional fields`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.A2)
                )

                val request = CreateConversationRequest(
                    topic = "Weekend plans",
                    additionalContext = null,
                    language = TestData.LANGUAGE,
                    tone = ConversationTone.NEUTRAL,
                    type = ConversationType.SMALL_TALK,
                    aiInterlocutorName = null,
                    aiInterlocutorAvatarId = null
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.additionalContext shouldBe ""
                response.body.aiInterlocutorName shouldBe null
                response.body.aiInterlocutorAvatarId shouldBe null
            }

            @Test
            fun `201 - should create multiple conversations for same user`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.C2)
                )

                val request1 = CreateConversationRequest(
                    topic = "First topic",
                    additionalContext = null,
                    language = TestData.LANGUAGE,
                    tone = TestData.TONE,
                    type = TestData.TYPE
                )

                val request2 = CreateConversationRequest(
                    topic = "Second topic",
                    additionalContext = null,
                    language = TestData.LANGUAGE,
                    tone = ConversationTone.FORMAL,
                    type = ConversationType.EXAM_PRACTICE
                )

                val response1 = conversationAPIClient.createConversation(
                    body = request1,
                    user = authenticatedUser
                )

                val response2 = conversationAPIClient.createConversation(
                    body = request2,
                    user = authenticatedUser
                )

                response1.status shouldBe HttpStatus.CREATED
                response2.status shouldBe HttpStatus.CREATED
                response1.body!!.id shouldNotBe response2.body!!.id
            }

            @Test
            fun `201 - should work with different conversation types`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                ConversationType.entries.forEach { type ->
                    val request = CreateConversationRequest(
                        topic = "Topic for ${type.name}",
                        additionalContext = null,
                        language = TestData.LANGUAGE,
                        tone = TestData.TONE,
                        type = type
                    )

                    val response = conversationAPIClient.createConversation(
                        body = request,
                        user = authenticatedUser
                    )

                    response.status shouldBe HttpStatus.CREATED
                    response.body!!.type shouldBe type
                }
            }

            @Test
            fun `201 - should work with different tones`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                ConversationTone.entries.forEach { tone ->
                    val request = CreateConversationRequest(
                        topic = "Topic with ${tone.name} tone",
                        additionalContext = null,
                        language = TestData.LANGUAGE,
                        tone = tone,
                        type = TestData.TYPE
                    )

                    val response = conversationAPIClient.createConversation(
                        body = request,
                        user = authenticatedUser
                    )

                    response.status shouldBe HttpStatus.CREATED
                    response.body!!.aiTone shouldBe tone
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot create conversation`() {
                val request = CreateConversationRequest(
                    topic = TestData.TOPIC,
                    additionalContext = null,
                    language = TestData.LANGUAGE,
                    tone = TestData.TONE,
                    type = TestData.TYPE
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - user has no proficiency for the language`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.GERMAN to LanguageProficiencyLevel.A1)
                )

                val request = CreateConversationRequest(
                    topic = TestData.TOPIC,
                    additionalContext = null,
                    language = LanguageName.ITALIAN,
                    tone = TestData.TONE,
                    type = TestData.TYPE
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - empty topic should fail`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val request = CreateConversationRequest(
                    topic = "",
                    additionalContext = null,
                    language = TestData.LANGUAGE,
                    tone = TestData.TONE,
                    type = TestData.TYPE
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - extremely long topic should fail`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val request = CreateConversationRequest(
                    topic = "a".repeat(501),
                    additionalContext = null,
                    language = TestData.LANGUAGE,
                    tone = TestData.TONE,
                    type = TestData.TYPE
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - extremely long additional context should fail`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val request = CreateConversationRequest(
                    topic = TestData.TOPIC,
                    additionalContext = "a".repeat(5001),
                    language = TestData.LANGUAGE,
                    tone = TestData.TONE,
                    type = TestData.TYPE
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - invalid aiInterlocutorAvatarId should fail`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val request = CreateConversationRequest(
                    topic = TestData.TOPIC,
                    additionalContext = null,
                    language = TestData.LANGUAGE,
                    tone = TestData.TONE,
                    type = TestData.TYPE,
                    aiInterlocutorName = "Dr. Smith",
                    aiInterlocutorAvatarId = "INVALID_AVATAR_ID"
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - extremely long aiInterlocutorName should fail`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val request = CreateConversationRequest(
                    topic = TestData.TOPIC,
                    additionalContext = null,
                    language = TestData.LANGUAGE,
                    tone = TestData.TONE,
                    type = TestData.TYPE,
                    aiInterlocutorName = "a".repeat(201),
                    aiInterlocutorAvatarId = TestData.AI_INTERLOCUTOR_AVATAR_ID
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - user with only one language cannot create in different language`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C2)
                )

                val request = CreateConversationRequest(
                    topic = "Conversation en français",
                    additionalContext = null,
                    language = LanguageName.FRENCH,
                    tone = TestData.TONE,
                    type = TestData.TYPE
                )

                val response = conversationAPIClient.createConversation(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[DELETE] /api/v1/conversations/{conversationId} - delete conversation")
    inner class DeleteConversationTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `204 - should delete conversation`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val created = createConversationEntity(userId = authenticatedUser.userInfo.id)

                val deleteResponse = conversationAPIClient.deleteConversation(
                    conversationId = created.id!!,
                    user = authenticatedUser
                )

                deleteResponse.status shouldBe HttpStatus.NO_CONTENT

                conversationRepository.findById(created.id!!).block() shouldBe null
            }

            @Test
            fun `204 - deleting conversation should not affect other user's conversations`() {
                val user1 = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )
                val user2 = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )

                val conversation1 = createConversationEntity(userId = user1.userInfo.id)
                val conversation2 = createConversationEntity(userId = user2.userInfo.id)

                conversationAPIClient.deleteConversation(
                    conversationId = conversation1.id!!,
                    user = user1
                )

                val getUser2Conversations = conversationAPIClient.getConversations(user = user2)
                getUser2Conversations.body!! shouldHaveSize 1
                getUser2Conversations.body[0].id shouldBe conversation2.id
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - anonymous user cannot delete conversation`() {
                val response = conversationAPIClient.deleteConversation(
                    conversationId = UUID.randomUUID(),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - should return 404 for non-existent conversation`() {
                val authenticatedUser = mockAuthenticatedUser()

                val response = conversationAPIClient.deleteConversation(
                    conversationId = UUID.randomUUID(),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - user cannot delete another user's conversation`() {
                val owner = mockAuthenticatedUser(
                    languages = mapOf(TestData.LANGUAGE to LanguageProficiencyLevel.B2)
                )
                val otherUser = mockAuthenticatedUser()

                val created = createConversationEntity(userId = owner.userInfo.id)

                val response = conversationAPIClient.deleteConversation(
                    conversationId = created.id!!,
                    user = otherUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND

                val verifyStillExists = conversationAPIClient.getConversationById(
                    conversationId = created.id!!,
                    user = owner
                )

                verifyStillExists.status shouldBe HttpStatus.OK
            }
        }
    }
}
