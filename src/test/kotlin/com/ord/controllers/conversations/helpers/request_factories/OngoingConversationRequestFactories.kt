package com.ord.controllers.conversations.helpers.request_factories

/*

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.util.*

class OngoingConversationRequestFactories(
    private val baseUrl: String,
    private val objectMapper: ObjectMapper,
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