package com.ord.controllers.conversations.helpers.request_factories

import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.controllers.conversations.helpers.unsafe_dtos.UnsafeSuggestConversationTopicRequest
import com.ord.features.conversation.models.enums.ConversationGoal
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import com.ord.testing_utils.mocks.games.GameMockerBase
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

class ConversationRequestFactory(
    private val baseUrl: String,
    private val objectMapper: ObjectMapper,
) {
    fun getSuggestTopicsRequest(
        authenticatedUser: MockedAuthenticatedUser? = null,
        clueFromUser: String? = null,
        language: String? = GameMockerBase.Companion.DefaultParams.language.toString(),
        conversationGoal: String? = ConversationGoal.SMALL_TALK.toString(),
    ): MockHttpServletRequestBuilder {
        val uri = "$baseUrl/suggest-topics"

        return MockMvcRequestBuilders
            .post(uri)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .contentType(MediaType.APPLICATION_JSON)
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
            .content(
                objectMapper.writeValueAsString(
                    UnsafeSuggestConversationTopicRequest(
                        clueFromUser = clueFromUser,
                        language = language,
                        conversationGoal = conversationGoal
                    )
                )
            )
    }
}