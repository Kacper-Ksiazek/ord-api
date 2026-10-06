package com.ord.features.home.model

import com.ord.features.home.model.parts.HomeOverviews
import com.ord.features.home.model.parts.HomeRecentContent
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Landing summary for the authenticated user and their selected learning language")
data class HomeResponse(
    @Schema(description = "Count cards and sparklines")
    val overviews: HomeOverviews,

    @Schema(description = "Recent words and conversations")
    val recentContent: HomeRecentContent,
)
