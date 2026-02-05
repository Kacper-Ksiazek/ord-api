package com.ord.core.gpt_tokens_usage_analytics.api

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.gpt_tokens_usage_analytics.facades.GptTokensUsageAnalyticsFacade
import com.ord.core.gpt_tokens_usage_analytics.requests.dto.AnalyticsPeriod
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageAnalyticsResponse
import com.ord.core.gpt_tokens_usage_analytics.responses.dto.GptTokensUsageRecordDTO
import com.ord.core.user.model.UserDTO
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/gpt-tokens-usage")
@Tag(
    name = "1. Core: GPT Tokens Usage Analytics",
    description = "View GPT tokens usage history and aggregated analytics (requires authentication)"
)
@SecurityRequirement(name = "bearer-jwt")
class GptTokensUsageAnalyticsController(
    private val facade: GptTokensUsageAnalyticsFacade
) {
    @GetMapping("/")
    @Operation(
        summary = "Get paginated GPT tokens usage records",
        description = "Retrieves a paginated list of GPT tokens usage records for the authenticated user, ordered by creation date descending."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Records retrieved successfully",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun getRecords(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(description = "Page number", example = "1") @RequestParam(required = false) page: Int?,
        @Parameter(description = "Number of items per page", example = "50") @RequestParam(required = false) perPage: Int?
    ): Mono<ResponseEntity<PaginatedDataResponse<GptTokensUsageRecordDTO>>> = facade.getRecords(
        userId = user.id,
        page = page,
        perPage = perPage
    )

    @GetMapping("/analytics")
    @Operation(
        summary = "Get aggregated GPT tokens usage analytics",
        description = "Returns daily time-series and per-operation-type breakdown of GPT tokens usage for the authenticated user within the specified period."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Analytics data retrieved successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = GptTokensUsageAnalyticsResponse::class))]
            ),
            ApiResponse(responseCode = "401", description = "Not authenticated", content = [Content()])
        ]
    )
    fun getAnalytics(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Parameter(description = "Time period for analytics", example = "LAST_7_DAYS") @RequestParam period: AnalyticsPeriod
    ): Mono<ResponseEntity<GptTokensUsageAnalyticsResponse>> = facade.getAnalytics(
        userId = user.id,
        period = period
    )
}
