package com.ord.features.conversation.api.facades

interface ConversationCRUDFacade {
    fun createConversation()

    fun deleteConversation()

    fun getConversationHistory()
}