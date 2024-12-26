package com.backend.ord.api.requests.games.bases

import java.util.*

interface GameToBeFinishedBase<UserAnswers> {
    val gameId: UUID

    /** In the format "HH:mm:ss" */
    val duration: String

    val userAnswers: UserAnswers
}