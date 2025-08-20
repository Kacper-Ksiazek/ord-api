package com.ord.controllers.conversations

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.user.UserRepository
import com.ord.core.user.model.UserMapper
import com.ord.controllers.conversations.helpers.request_factories.OngoingConversationRequestFactories
import com.ord.controllers.conversations.seeders.ConversationSeeder
import com.ord.features.conversation.models.entities.ConversationEntity
import com.ord.testing_utils.api.SSETestingUtils
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@DisplayName("- ConversationController")
class TestOngoingConversationController @Autowired constructor(
    private val conversationSeeder: ConversationSeeder,

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
    private val ongoingConversationRequestFactory = OngoingConversationRequestFactories(
        baseUrl = "/api/v1/conversations/ongoing",
        objectMapper = objectMapper,
    )

    private val sse: SSETestingUtils by lazy {
        SSETestingUtils(
            mockMvc = mockMvc,
            objectMapper = objectMapper
        )
    }

    @Nested
    @DisplayName("[POST] /api/v1/conversations/ongoing/initialize-by-ai - let AI create a first message in the conversation")
    inner class InitConversationWithAI {
        @Nested
        @DisplayName("Positive")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
        inner class Positive {
            lateinit var authenticatedUser: MockedAuthenticatedUser
            lateinit var conversation: ConversationEntity

            /** First message from AI in the conversation; response from initialization request */
            lateinit var responseFromAI: String

            @BeforeAll
            fun beforeAll() {
                authenticatedUser = mockAuthenticatedUser()
                conversation = conversationSeeder.seedOneEntity(authenticatedUser)

                val request = ongoingConversationRequestFactory.getInitConversationByAIRequest(
                    authenticatedUser = authenticatedUser,
                    conversationId = conversation.id,
                )

                responseFromAI = sse.postStringChunks(
                    request = request,
                )
            }

            @Test
            fun `test test test`() {
                1 shouldBe 1
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

        }
    }
}