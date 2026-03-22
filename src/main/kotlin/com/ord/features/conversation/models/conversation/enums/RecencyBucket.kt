package com.ord.features.conversation.models.conversation.enums

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Grouping bucket based on last activity (UTC)")
@ExportToOpenAPI
enum class RecencyBucket {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    THIS_MONTH,
    LATER
}
