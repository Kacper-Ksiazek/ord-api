package com.ord.features.conversation.api.facades

interface OngoingConversationFacade {
    /**
     * Initializes conversation by either AI or USER
     */
    fun initializeConversation()

    /**
     * Calls AI API to request a message in conversation from the AI
     */
    fun requestAIMessage()

    /**
     * Performs a grammar and style review of a single user message, independently
     * of the current conversation context.
     */
    fun saveUserMessageAndGetFeedback()
}