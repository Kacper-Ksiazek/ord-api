package com.backend.ord.controllers.games

import com.backend.ord.api.requests.games.data.StartGameRequestData
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/games/words-typing")
class WordsTypingGameController {

    @PostMapping("/start")
    fun startCrosswordGame(
        request: HttpServletRequest,
        @Valid @RequestBody body: StartGameRequestData
//    ): ResponseEntity<StartedCrosswordGameResponse> {
    ) {
        return
    }
}
