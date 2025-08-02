package com.ord.features.conversation.api.facades

interface OngoingConversationFacade {
    /**
     * Initializes conversation by AI - AI will write a greeting message
     */
    fun initializeConversationByAI()

    /**
     * Handles a message sent by the user and appends it to the ongoing conversation.
     *
     * This method supports both starting a new conversation (if none exists for the user)
     * and continuing an existing one. It persists the user message, forwards the full
     * conversation context to the AI service, stores the AI response, and returns it.
     */
    fun handleUserMessage()

    /**
     * Performs a grammar and style review of a single user message, independently
     * of the current conversation context.
     */
    fun reviewUserMessage()
}