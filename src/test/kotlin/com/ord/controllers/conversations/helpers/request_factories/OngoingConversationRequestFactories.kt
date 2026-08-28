package com.ord.controllers.conversations.helpers.request_factories

/*

import tools.jackson.databind.json.JsonMapper
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.util.*

class OngoingConversationRequestFactories(
    private val baseUrl: String,
    private val objectMapper: JsonMapper,
) {
    fun getInitConversationByAIRequest(
        conversationId: UUID?,
        authenticatedUser: MockedAuthenticatedUser? = null,
    ): MockHttpServletRequestBuilder {
        val uri = "$baseUrl/initialize-by-ai?conversationId=${conversationId ?: ""}"

        return MockMvcRequestBuilders
            .post(uri)
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
    }
}

 */