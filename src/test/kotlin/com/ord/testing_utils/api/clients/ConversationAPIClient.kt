package com.ord.testing_utils.api.clients

import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import com.ord.features.conversation.models.dto.ConversationDTO
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

class ConversationAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/conversations"

    fun suggestTopics(
        body: SuggestConversationTopicRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<String?> {
        return post(
            url = "$baseUrl/suggest-topics",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<String>() {}
        )
    }

    fun getConversations(
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<List<ConversationDTO>?> {
        return get(
            url = "$baseUrl/",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<List<ConversationDTO>>() {}
        )
    }

    fun getConversationById(
        conversationId: UUID,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<ConversationDTO?> {
        return get(
            url = "$baseUrl/$conversationId",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<ConversationDTO>() {}
        )
    }

    fun createConversation(
        body: CreateConversationRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<ConversationDTO?> {
        return post(
            url = "$baseUrl/",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<ConversationDTO>() {}
        )
    }

    fun deleteConversation(
        conversationId: UUID,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<Unit?> {
        return delete<Unit>(
            url = "$baseUrl/$conversationId",
            user = user
        )
    }
}