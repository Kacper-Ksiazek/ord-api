package com.backend.ord.features.game.variants.shared.dto.api_requests.bases

import java.util.*

interface FinishGameRequestDataBase<TAnswers> {
    val gameId: UUID
    val duration: String
    val answers: TAnswers
}

data class UnsafeFinishGameRequestData<TAnswers>(
    val gameId: UUID? = null,
    val duration: String? = null,
    val answers: TAnswers? = null
)
