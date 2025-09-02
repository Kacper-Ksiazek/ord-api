package com.ord.core.word.api.facades.impl

import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.facades.WordPropertyToggleFacade
import com.ord.core.word.api.requests.dto.WordBulkActionRequest
import com.ord.core.word.api.requests.enums.WordToggleableProperty
import com.ord.core.word.service.WordService
import com.ord.shared.extensions.convertToSetExplicitly
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
        user: UserEntity
    ): Mono<Void> {
        return wordService.toggleProperty(
            wordId = id,
            userId = user.id,
            property = property
        ).then()
    }

    override fun togglePropertyForMultipleWords(
        body: WordBulkActionRequest,
        property: WordToggleableProperty,
        user: UserEntity
    ): Mono<Void> {
        return wordService.togglePropertyForManyWords(
            wordIds = body.ids.convertToSetExplicitly(paramName = "ids"),
            userId = user.id,
            property = property
        ).then()
    }
}