package com.ord.testing_utils.api.clients

import com.ord.features.conversation.api.facades.helpers.ai_responses.GeneratedAIInterlocutorData
import com.ord.features.conversation.api.requests.CreateConversationRequest
import com.ord.features.conversation.api.requests.GenerateAIInterlocutorDataRequest
import com.ord.features.conversation.api.requests.SuggestConversationTopicRequest
import com.ord.features.conversation.models.conversation.ConversationDTO
import com.ord.features.conversation.models.conversation.ConversationSummaryDTO
import com.ord.features.conversation.models.conversation.enums.ConversationType
import com.ord.features.conversation.models.conversation.enums.RecencyBucket
import com.ord.features.conversation.models.conversation_activity.ConversationActivityOverviewDTO
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

class ConversationAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/conversations"

    fun suggestTopics(
        body: SuggestConversationTopicRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<String?> {
        return post(
            url = "$baseUrl/suggest-topics",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<String>() {}
        )
    }

    fun generateAIInterlocutor(
        body: GenerateAIInterlocutorDataRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<GeneratedAIInterlocutorData?> {
        return post(
            url = "$baseUrl/suggest-ai-interlocutor",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<GeneratedAIInterlocutorData>() {}
        )
    }

    fun getConversations(
        user: MockedAuthenticatedUser? = null,
        search: String? = null,
        recencyBucket: RecencyBucket? = null,
        type: ConversationType? = null,
    ): APIClientResponse<List<ConversationSummaryDTO>?> {
        val queryParams = buildMap<String, String> {
            search?.let { put("search", it) }
            recencyBucket?.let { put("recencyBucket", it.name) }
            type?.let { put("type", it.name) }
        }.ifEmpty { null }
        return get(
            url = "$baseUrl/",
            user = user,
            queryParams = queryParams,
            responseBodyType = object : ParameterizedTypeReference<List<ConversationSummaryDTO>>() {}
        )
    }

    fun getConversationById(
        conversationId: UUID,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<ConversationDTO?> {
        return get(
            url = "$baseUrl/$conversationId",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<ConversationDTO>() {}
        )
    }

    fun createConversation(
        body: CreateConversationRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<ConversationDTO?> {
        return post(
            url = "$baseUrl/",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<ConversationDTO>() {}
        )
    }

    fun getActivityOverview(
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<ConversationActivityOverviewDTO?> {
        return get(
            url = "$baseUrl/overview",
            user = user,
            responseBodyType = object : ParameterizedTypeReference<ConversationActivityOverviewDTO>() {}
        )
    }

    fun deleteConversation(
        conversationId: UUID,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Unit?> {
        return delete<Unit>(
            url = "$baseUrl/$conversationId",
            user = user
        )
    }
}