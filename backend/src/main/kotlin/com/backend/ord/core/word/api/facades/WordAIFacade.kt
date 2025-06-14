package com.backend.ord.core.word.api.facades

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.requests.dto.GenerateWordManualRequest
import com.backend.ord.core.word.api.responses.dto.AIGeneratedWordManual

interface WordAIFacade {
    fun generateWordManual(body: GenerateWordManualRequest, user: UserEntity): AIGeneratedWordManual
}