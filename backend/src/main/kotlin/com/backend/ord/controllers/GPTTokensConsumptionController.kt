package com.backend.ord.controllers

import com.backend.ord.api.responses.gpt_tokens_usage.DetailedWordTokensUsage
import com.backend.ord.api.responses.gpt_tokens_usage.TokensUsageStatistics
import com.backend.ord.api.responses.gpt_tokens_usage.TokensUsageWithinTimePeriod
import com.backend.ord.api.responses.gpt_tokens_usage.toDetailedWordTokensUsage
import com.backend.ord.core.auth.jwt.JwtService
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.services.gpt_tokens_usage.WordTokensUsageService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/gpt-tokens-consumption")
class GPTTokensConsumptionController(
    private val jwtService: JwtService,
    private val wordsTokensUsageService: WordTokensUsageService,
) {
    private val calendar: Calendar = Calendar.getInstance()

    private val currentMonth: Int = calendar.get(Calendar.MONTH) + 1
    private val currentYear: Int = calendar.get(Calendar.YEAR)

    // TODO: Implement following endpoints:

    // To retrieve detailed information about GPT tokens consumption for the current user:

    // 1. GET / - Get all GPT tokens consumption ( words, stories, games ) for the current user
    // 3. GET /stories - Get all GPT tokens consumption for stories for the current user
    // 4. GET /games - Get all GPT tokens consumption for games for the current user

    // TODO: To retrieve summarized & aggregated data

    // 5. GET /summary - Get summary of GPT tokens consumption for the current user
    //                   Total cost & number of operations by category - words, stories, games
    // 6. GET /summary/words - Get summary of GPT tokens consumption for words for the current user ( grouped per consumption type )
    // 7. GET /summary/stories - Get summary of GPT tokens consumption for stories for the current user ( grouped per consumption type )
    // 8. GET /summary/games - Get summary of GPT tokens consumption for games for the current user ( grouped per consumption type )
    @GetMapping("/words-detailed")
    fun getWordsConsumption(
        request: HttpServletRequest,
        @RequestParam month: Int = currentMonth,
        @RequestParam year: Int = currentYear,
    ): ResponseEntity<TokensUsageWithinTimePeriod<DetailedWordTokensUsage>> {
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        return ResponseEntity.ok(
            TokensUsageWithinTimePeriod(
                month = month,
                year = year,
                data = wordsTokensUsageService.getDetailedConsumption(
                    userId = user.id,
                    month = month,
                    year = year
                ).map { it.toDetailedWordTokensUsage() }
            )
        )
    }

    @GetMapping("/words-summary")
    fun getWordsConsumptionSummary(
        request: HttpServletRequest,
        @RequestParam month: Int = currentMonth,
        @RequestParam year: Int = currentYear,
    ): ResponseEntity<TokensUsageWithinTimePeriod<TokensUsageStatistics<WordsGPTTokensConsumptionType>>> {
        val user: UserEntity = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        return ResponseEntity.ok(
            TokensUsageWithinTimePeriod(
                month = month,
                year = year,
                data = wordsTokensUsageService.getConsumptionStatistics(
                    userId = user.id,
                    month = month,
                    year = year
                )
            )
        )
    }
}