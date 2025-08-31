package com.ord.controllers.conversations

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.user.model.UserMapper
import com.ord.controllers.conversations.helpers.request_factories.ConversationRequestFactory
import com.ord.core.ai_provider.dto.helpers.StreamSimpleItem
import com.ord.testing_utils.api.SSETestingUtils
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- ConversationController")
class TestConversationController @Autowired constructor(
    objectMapper: ObjectMapper,
    mockMvc: MockMvc,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userMapper: UserMapper,
    userRepository: UserRepository
) : ControllerTestBase(
    objectMapper = objectMapper,
    mockMvc = mockMvc,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userMapper = userMapper,
    userRepository = userRepository
) {
    private val conversationRequestFactory = ConversationRequestFactory(
        baseUrl = "/api/v1/conversations",
        objectMapper = objectMapper,
    )

    private val sse: SSETestingUtils by lazy {
        SSETestingUtils(
            mockMvc = mockMvc,
            objectMapper = objectMapper
        )
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/suggest-topics - suggest some conversation topics")
    inner class PostSuggestTopics {

        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            lateinit var chunks: List<StreamSimpleItem>
            lateinit var finalContent: List<StreamSimpleItem>

            @BeforeAll
            fun beforeAll() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = conversationRequestFactory.getSuggestTopicsRequest(
                    authenticatedUser = authenticatedUser,
                )

                val response = sse.postStructuralChunks(
                    request = request,
                    chunkType = object : TypeReference<StreamSimpleItem>() {},
                    finalType = object : TypeReference<List<StreamSimpleItem>>() {}
                )

                response shouldNotBe null

                chunks = response!!.first
                finalContent = response.second
            }


            @Test
            fun `200 - There should be exactly 3 suggestions`() {
                val expectedNumberOfSuggestions = 3

                chunks shouldHaveSize expectedNumberOfSuggestions
                finalContent shouldHaveSize expectedNumberOfSuggestions
            }

            @Test
            fun `200 - All chunks should be included in the final content`() {
                finalContent shouldContainAll chunks
            }

            @Test
            fun `200 - Not passing clue from user is allowed`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = conversationRequestFactory.getSuggestTopicsRequest(
                    authenticatedUser = authenticatedUser,
                    clueFromUser = null,
                )

                val response = sse.postStructuralChunks(
                    request = request,
                    chunkType = object : TypeReference<StreamSimpleItem>() {},
                    finalType = object : TypeReference<List<StreamSimpleItem>>() {}
                )
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Anonymous user cannot get topic suggestions`() {
                val request = conversationRequestFactory.getSuggestTopicsRequest()

                sse.postStructuralChunks(
                    expectedStatus = HttpStatus.FORBIDDEN,
                    request = request,
                    chunkType = object : TypeReference<StreamSimpleItem>() {},
                    finalType = object : TypeReference<List<StreamSimpleItem>>() {}
                )
            }

            @Test
            fun `400 - Invalid language`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = conversationRequestFactory.getSuggestTopicsRequest(
                    authenticatedUser = authenticatedUser,
                    language = "invalid_language"
                )

                sse.postStructuralChunks(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    request = request,
                    chunkType = object : TypeReference<StreamSimpleItem>() {},
                    finalType = object : TypeReference<List<StreamSimpleItem>>() {}
                )
            }

            @Test
            fun `400 - Invalid conversation goal`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = conversationRequestFactory.getSuggestTopicsRequest(
                    authenticatedUser = authenticatedUser,
                    conversationGoal = "invalid_goal"
                )

                sse.postStructuralChunks(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    request = request,
                    chunkType = object : TypeReference<StreamSimpleItem>() {},
                    finalType = object : TypeReference<List<StreamSimpleItem>>() {}
                )
            }

            @Test
            fun `400 - Too long clueFromUser`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = conversationRequestFactory.getSuggestTopicsRequest(
                    authenticatedUser = authenticatedUser,
                    clueFromUser = "x".repeat(260),
                )

                sse.postStructuralChunks(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    request = request,
                    chunkType = object : TypeReference<StreamSimpleItem>() {},
                    finalType = object : TypeReference<List<StreamSimpleItem>>() {}
                )
            }

            @Test
            fun `400 - Empty conversation goal`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = conversationRequestFactory.getSuggestTopicsRequest(
                    authenticatedUser = authenticatedUser,
                    conversationGoal = null,
                )

                sse.postStructuralChunks(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    request = request,
                    chunkType = object : TypeReference<StreamSimpleItem>() {},
                    finalType = object : TypeReference<List<StreamSimpleItem>>() {}
                )
            }

            @Test
            fun `400 - Empty language`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = conversationRequestFactory.getSuggestTopicsRequest(
                    authenticatedUser = authenticatedUser,
                    language = null,
                )

                sse.postStructuralChunks(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    request = request,
                    chunkType = object : TypeReference<StreamSimpleItem>() {},
                    finalType = object : TypeReference<List<StreamSimpleItem>>() {}
                )
            }
        }
    }
}