package com.ord.core.word.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.responses.dto.AIGeneratedWordManual
import reactor.core.publisher.Mono

interface WordAIFacade {
    fun generateWordManual(
        body: GenerateWordManualRequest,
        user: UserEntity
    ): Mono<AIGeneratedWordManual>
}