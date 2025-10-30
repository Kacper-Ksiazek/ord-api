package com.ord.core.word.api.ai.facades

import com.ord.core.user.model.UserDTO
import com.ord.core.word.api.ai.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.ai.requests.dto.SuggestVocabularyRequest
import com.ord.core.word.api.ai.responses.dto.AIGeneratedWordManual
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface WordAIFacade {
    fun generateWordManual(
        body: GenerateWordManualRequest,
        user: UserDTO,
    ): Mono<AIGeneratedWordManual>

    fun suggestVocabulary(
        body: SuggestVocabularyRequest,
        user: UserDTO,
    ): Flux<String>
}