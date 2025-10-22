package com.ord.core.word.api.crud.facades.impl

import com.ord.core.word.api.crud.facades.WordPropertyToggleFacade
import com.ord.core.word.api.crud.requests.dto.WordBulkActionRequest
import com.ord.core.word.api.crud.requests.enums.WordToggleableProperty
import com.ord.core.word.services.WordService
import com.ord.shared.extensions.convertToSetExplicitly
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class WordPropertyToggleFacadeImpl(
    private val wordService: WordService
) : WordPropertyToggleFacade {
    override fun togglePropertyForOneWord(
        id: UUID,
        property: WordToggleableProperty,
        userId: UUID
    ): Mono<ResponseEntity<Unit>> {
        return wordService
            .toggleProperty(
                wordId = id,
                userId = userId,
                property = property
            )
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build() })

    }

    override fun togglePropertyForMultipleWords(
        body: WordBulkActionRequest,
        property: WordToggleableProperty,
        userId: UUID
    ): Mono<ResponseEntity<Unit>> {
        return wordService
            .togglePropertyForManyWords(
                wordIds = body.ids.convertToSetExplicitly(paramName = "ids"),
                userId = userId,
                property = property
            )
            .then(Mono.fromCallable { ResponseEntity.status(HttpStatus.OK).build() })

    }
}