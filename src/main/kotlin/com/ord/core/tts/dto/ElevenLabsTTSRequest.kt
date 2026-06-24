package com.ord.core.tts.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ElevenLabsTTSRequest(
    val text: String,
    @JsonProperty("model_id")
    val modelId: String,
)
