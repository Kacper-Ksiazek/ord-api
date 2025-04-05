package com.backend.ord.controllers.unsafe_api_requests

import com.backend.ord.api.requests.games.CrosswordUserAnswers
import java.util.*

data class UnsafeFinishCrosswordGameRequestData(
    val gameId: UUID? = null,
    val duration: String? = null,
    val userAnswers: CrosswordUserAnswers? = null
)