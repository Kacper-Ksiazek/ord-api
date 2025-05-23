package com.backend.ord.testing_utils.extensions

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.repositories.UserActivityLogRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.*

fun UserActivityLogRepository.assertUserActivityLogForCompletingGame(
    expectedType: UserActivityType,
    language: LanguageName,
    difficulty: GameDifficulty,
    userId: UUID,
) {
    val logs = findAllForUser(userId)

    logs shouldHaveSize 1

    logs.first().let {
        it.type shouldBe expectedType
        it.language shouldBe language
        it.points shouldBe expectedType.points
        it.gameDifficulty shouldBe difficulty
    }
}
