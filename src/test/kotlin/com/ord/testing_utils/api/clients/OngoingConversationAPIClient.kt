package com.ord.testing_utils.api.clients

import com.ord.features.conversation.api.facades.helpers.ai_responses.ReviewedUserConversationMessage
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.ReviewUserConversationMessageRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUserUpdated
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

class OngoingConversationAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/conversations/ongoing"

    fun initializeConversationByAI(
        conversationId: UUID,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<String?> {
        return post(
            url = "$baseUrl/initialize-by-ai?conversationId=$conversationId",
            body = null,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<String>() {}
        )
    }

    fun requestAIMessage(
        body: CreateAIConversationMessageRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<String?> {
        return post(
            url = "$baseUrl/request-ai-message",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<String>() {}
        )
    }

    fun handleUserMessage(
        body: ReviewUserConversationMessageRequest,
        user: MockedAuthenticatedUserUpdated? = null
    ): APIClientResponse<ReviewedUserConversationMessage?> {
        return post(
            url = "$baseUrl/handle-user-message",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<ReviewedUserConversationMessage>() {}
        )
    }
}