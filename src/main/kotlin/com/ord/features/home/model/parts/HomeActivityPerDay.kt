package com.ord.features.home.model.parts

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Days in the current UTC calendar year with any learning activity")
data class HomeActivityPerDay(
    @Schema(description = "Current calendar year in UTC", example = "2026")
    val year: Int,

    @Schema(description = "Days in that year whose count is greater than zero")
    val days: List<HomeActivityDay>,
)
