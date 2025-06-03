package com.backend.ord.core.word.api

import com.backend.ord.core.auth.security.AuthenticatedUser
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.facades.WordAIFacade
import com.backend.ord.core.word.api.requests.dto.GenerateWordManualRequest
import com.backend.ord.core.word.api.responses.dto.AIGeneratedWordManual
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/words/ai")
class WordAIController(
    private val wordAIFacade: WordAIFacade
) {
    @PostMapping("/generate-manual")
    fun generateAIManual(
        @AuthenticatedUser user: UserEntity,
        @Valid @RequestBody body: GenerateWordManualRequest
    ): ResponseEntity<AIGeneratedWordManual> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(wordAIFacade.generateWordManual(body, user))
    }
}