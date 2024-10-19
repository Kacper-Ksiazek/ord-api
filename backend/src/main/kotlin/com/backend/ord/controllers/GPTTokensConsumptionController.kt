package com.backend.ord.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.backend.ord.config.security.JwtService
import com.backend.ord.services.gpt_tokens_usage.WordTokensUsageService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.RequestParam
import java.time.Month

@RestController
@RequestMapping("/api/v1/gpt-tokens-consumption")
class GPTTokensConsumptionController(
    private val jwtService: JwtService,
    private val wordsTokensUsageService: WordTokensUsageService
) {
    // TODO: Implement following endpoints:

    // To retrieve detailed information about GPT tokens consumption for the current user:

    // 1. GET / - Get all GPT tokens consumption ( words, stories, games ) for the current user
    // 2. GET /words - Get all GPT tokens consumption for words for the current user
    // 3. GET /stories - Get all GPT tokens consumption for stories for the current user
    // 4. GET /games - Get all GPT tokens consumption for games for the current user

    // TODO: To retrieve summarized & aggregated data

    // 5. GET /summary - Get summary of GPT tokens consumption for the current user
    //                   Total cost & number of operations by category - words, stories, games
    // 6. GET /summary/words - Get summary of GPT tokens consumption for words for the current user ( grouped per consumption type )
    // 7. GET /summary/stories - Get summary of GPT tokens consumption for stories for the current user ( grouped per consumption type )
    // 8. GET /summary/games - Get summary of GPT tokens consumption for games for the current user ( grouped per consumption type )

    @GetMapping("/words")
    fun getWordsConsumption(
        request: HttpServletRequest,
        @RequestParam month: Month,
        @RequestParam year: Int
    ): String {
        val user = jwtService.getAuthenticatedUser(request)!!

        return wordsTokensUsageService.getTokensConsumptionForUserInMonth(user.id, month, year).toString()
    }
}