package com.ord.features.home.model.parts

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "One UTC day of home activity")
data class HomeActivityDay(
    @Schema(description = "UTC date", example = "2026-09-30")
    val date: String,

    @Schema(description = "Events recorded that day", example = "4")
    val count: Long,
)
