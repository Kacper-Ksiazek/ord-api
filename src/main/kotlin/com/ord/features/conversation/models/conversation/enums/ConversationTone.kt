package com.ord.features.conversation.models.conversation.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class ConversationTone {
    FRIENDLY,
    FORMAL,
    HUMOROUS,
    NEUTRAL,
    ENCOURAGING,
    CHALLENGING
}