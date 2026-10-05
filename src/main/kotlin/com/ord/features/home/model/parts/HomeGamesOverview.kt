package com.ord.features.home.model.parts

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Games card. comingSoon stays true until the games UI ships.")
data class HomeGamesOverview(
    @Schema(description = "Games practice is not on the home screen yet", example = "true")
    val comingSoon: Boolean,

    @Schema(description = "Finished games stored for this user and language. Null when that count is not available.", nullable = true)
    val total: Long?,

    @Schema(description = "Finished games recorded in the current UTC calendar month. Null when that count is not available.", nullable = true)
    val last30Days: Long?,

    @Schema(description = "Finished games per UTC day for the last 90 days, including zeros")
    val trend: List<HomeActivityDay>,
)
