package com.backend.ord.core.word.api.facades

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.requests.dto.WordBulkActionRequest
import com.backend.ord.core.word.api.requests.enums.WordToggleableProperty
import java.util.*

interface WordPropertyToggleFacade {
    fun togglePropertyForOneWord(id: UUID, property: WordToggleableProperty, user: UserEntity)

    fun togglePropertyForMultipleWords(body: WordBulkActionRequest, property: WordToggleableProperty, user: UserEntity)
}