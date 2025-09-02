package com.ord.core.word.api.facades

import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.dto.WordBulkActionRequest
import com.ord.core.word.api.requests.enums.WordToggleableProperty
import reactor.core.publisher.Mono
import java.util.*

interface WordPropertyToggleFacade {
    fun togglePropertyForOneWord(
        id: UUID,
        property: WordToggleableProperty,
        user: UserEntity
    ): Mono<Void>

    fun togglePropertyForMultipleWords(
        body: WordBulkActionRequest,
        property: WordToggleableProperty,
        user: UserEntity
    ): Mono<Void>
}