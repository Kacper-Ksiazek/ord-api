package com.ord.testing_utils.extensions

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.repository.UserActivityLogRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.*

fun UserActivityLogRepository.assertUserActivityLogForCompletingGame(
    expectedType: UserActivityType,
    language: LanguageName,
    difficulty: GameDifficulty,
    userId: UUID,
) {
    val logs = findAllByUserId(userId).collectList().block()!!

    logs shouldHaveSize 1

    logs.first().let {
        it.type shouldBe expectedType
        it.language shouldBe language
        it.points shouldBe expectedType.points
        it.gameDifficulty shouldBe difficulty
    }
}
