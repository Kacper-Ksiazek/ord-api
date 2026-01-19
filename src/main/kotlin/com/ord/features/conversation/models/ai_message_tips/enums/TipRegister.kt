package com.ord.features.conversation.models.ai_message_tips.enums

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Formality register indicator for learning tips")
@ExportToOpenAPI
enum class TipRegister {
    FORMAL,      // Business, academic, official
    NEUTRAL,     // Everyday conversation
    COLLOQUIAL   // Very casual, slangy
}
