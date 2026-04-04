package com.ord.testing_utils.api.clients

import com.ord.features.conversation.api.facades.helpers.ai_responses.AIMessageLearningTips
import com.ord.features.conversation.api.facades.helpers.ai_responses.ConversationUserMessageAnalysisPayload
import com.ord.features.conversation.api.requests.CreateAIConversationMessageRequest
import com.ord.features.conversation.api.requests.GetAnalysisForUserConversationMessageRequest
import com.ord.features.conversation.api.requests.GetLearningTipsForAIMessageRequest
import com.ord.features.conversation.api.requests.SaveUserConversationMessageRequest
import com.ord.features.conversation.models.conversation_message.ConversationMessageDTO
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

class OngoingConversationAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/conversations/ongoing"

    fun initializeConversationByAI(
        conversationId: UUID,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<String?> {
        return post(
            url = "$baseUrl/ai/initialize?conversationId=$conversationId",
            body = null,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<String>() {}
        )
    }

    fun requestAIMessage(
        body: CreateAIConversationMessageRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<String?> {
        return post(
            url = "$baseUrl/ai/request-message",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<String>() {}
        )
    }

    fun saveUserMessage(
        body: SaveUserConversationMessageRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<ConversationMessageDTO?> {
        return post(
            url = "$baseUrl/user/save-message",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<ConversationMessageDTO>() {}
        )
    }

    fun generateAnalysis(
        body: GetAnalysisForUserConversationMessageRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<ConversationUserMessageAnalysisPayload?> {
        return post(
            url = "$baseUrl/user/generate-analysis",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<ConversationUserMessageAnalysisPayload>() {}
        )
    }

    fun generateLearningTips(
        body: GetLearningTipsForAIMessageRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<AIMessageLearningTips?> {
        return post(
            url = "$baseUrl/ai/generate-learning-tips",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<AIMessageLearningTips>() {}
        )
    }
}