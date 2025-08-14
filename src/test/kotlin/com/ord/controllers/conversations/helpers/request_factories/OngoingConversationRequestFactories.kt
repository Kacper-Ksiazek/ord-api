package com.ord.controllers.conversations.helpers.request_factories

import com.fasterxml.jackson.databind.ObjectMapper

class OngoingConversationRequestFactories(
    private val baseUrl: String,
    private val objectMapper: ObjectMapper,
) {
}