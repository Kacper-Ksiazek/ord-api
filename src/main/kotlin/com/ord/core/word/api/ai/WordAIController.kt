package com.ord.core.word.api.ai

import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.ai.facades.WordAIFacade
import com.ord.core.word.api.ai.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.ai.responses.dto.AIGeneratedWordManual
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/words/ai")
class WordAIController(
    private val wordAIFacade: WordAIFacade
) {
    @PostMapping("/generate-manual")
    fun generateAIManual(
        @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: GenerateWordManualRequest
    ): Mono<ResponseEntity<AIGeneratedWordManual>> {
        return wordAIFacade.generateWordManual(body, user)
            .map { ResponseEntity.status(HttpStatus.OK).body(it) }
    }
}