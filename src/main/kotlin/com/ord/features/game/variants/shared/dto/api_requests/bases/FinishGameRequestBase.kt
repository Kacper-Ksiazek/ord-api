package com.ord.features.game.variants.shared.dto.api_requests.bases

import jakarta.validation.constraints.Pattern
import java.util.*

abstract class FinishGameRequestDataBase<TAnswers>(
    open val gameId: UUID,

    @field:Pattern(
        regexp = """^\d{2}:\d{2}:\d{2}$""",
        message = "Duration must be in format hh:mm:ss"
    )
    open val duration: String
) {
    abstract val answers: TAnswers
}

data class UnsafeFinishGameRequestData<TAnswers>(
    val gameId: UUID? = null,
    val duration: String? = null,
    val answers: TAnswers? = null
)
