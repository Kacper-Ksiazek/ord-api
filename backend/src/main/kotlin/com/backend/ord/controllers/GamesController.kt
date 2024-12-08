package com.backend.ord.controllers

import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.entities.User
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.services.ai.AIGameService
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/games")
class GamesController(
    private val aIGameService: AIGameService,
    private val jwtService: JwtService,
) {
    @PostMapping("/start")
    fun startGame(request: HttpServletRequest): ResponseEntity<*> {
        val user: User = jwtService.getAuthenticatedUserOrThrowForbidden(request)

        val aiGeneratedCrosswordBase: AIGeneratedCrossword = aIGameService.generateCrosswordGame(
            user = user,
            language = LanguageName.ENGLISH,
            difficulty = GameDifficulty.HARD
        )

        return ResponseEntity.ok(
            aiGeneratedCrosswordBase
        )
    }

}