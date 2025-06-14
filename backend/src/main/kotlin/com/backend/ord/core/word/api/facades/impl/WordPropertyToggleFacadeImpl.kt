package com.backend.ord.core.word.api.facades.impl

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.facades.WordPropertyToggleFacade
import com.backend.ord.core.word.api.requests.dto.WordBulkActionRequest
import com.backend.ord.core.word.api.requests.enums.WordToggleableProperty
import com.backend.ord.core.word.service.WordService
import com.backend.ord.shared.extensions.convertToSetExplicitly
import org.springframework.stereotype.Component
import java.util.*

@Component
class WordPropertyToggleFacadeImpl(
    private val wordService: WordService
) : WordPropertyToggleFacade {
    override fun togglePropertyForOneWord(
        id: UUID,
        property: WordToggleableProperty,
        user: UserEntity
    ) {
        wordService.toggleProperty(
            wordId = id,
            userId = user.id,
            property = property
        )
    }

    override fun togglePropertyForMultipleWords(
        body: WordBulkActionRequest,
        property: WordToggleableProperty,
        user: UserEntity
    ) {
        wordService.togglePropertyForManyWords(
            wordIds = body.ids.convertToSetExplicitly(paramName = "ids"),
            userId = user.id,
            property = property
        )
    }
}