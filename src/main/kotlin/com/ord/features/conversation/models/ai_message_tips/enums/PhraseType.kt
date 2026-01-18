package com.ord.features.conversation.models.ai_message_tips.enums

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Type of phrase learning tip")
@ExportToOpenAPI
enum class PhraseType {
    IDIOMATIC,   // True idioms with figurative meaning (e.g., "break the ice", "spill the beans")
    LITERAL      // Collocations, expressions, and useful phrases with literal meaning
}
