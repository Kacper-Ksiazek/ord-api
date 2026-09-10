package com.ord.core.word.api.capture.requests.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Request to activate multiple captured words")
data class ActivateManyWordsRequest(
    val ids: List<UUID>,
)
