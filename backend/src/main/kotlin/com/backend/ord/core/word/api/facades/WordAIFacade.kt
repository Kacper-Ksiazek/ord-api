package com.backend.ord.core.word.api.facades

import com.backend.ord.api.responses.GenerateWordManualAIResponse
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.api.requests.dto.GenerateWordManualRequest

interface WordAIFacade {
    fun generateWordManual(body: GenerateWordManualRequest, user: UserEntity): GenerateWordManualAIResponse
}