package com.backend.ord.api.requests.games.bases

import java.util.*

interface GameToBeFinishedBase<UserAnswers> {
    val gameId: UUID
    val userAnswers: UserAnswers
}