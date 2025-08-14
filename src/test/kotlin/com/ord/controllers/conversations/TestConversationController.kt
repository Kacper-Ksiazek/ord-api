package com.ord.controllers.conversations

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.user.UserRepository
import com.ord.core.user.model.UserMapper
import com.ord.controllers.conversations.helpers.request_factories.ConversationRequestFactory
import com.ord.testing_utils.api.RESTTestingUtils
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
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

    private val sse: RESTTestingUtils by lazy {
        RESTTestingUtils(
            mockMvc = mockMvc,
            objectMapper = objectMapper
        )
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/suggest-topics - suggest some conversation topics")
    inner class PostSuggestTopics {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `testing test`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = conversationRequestFactory.getSuggestTopicsRequest(
                    authenticatedUser = authenticatedUser,
                )

                sse.postSSERequest<Unit>(request = request)
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

        }
    }
}