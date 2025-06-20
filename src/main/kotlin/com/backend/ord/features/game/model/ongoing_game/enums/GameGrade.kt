package com.backend.ord.features.game.model.ongoing_game.enums

import com.backend.ord.shared.utils.data_classes.Percentage

enum class GameGrade(val threshold: Double) {
    /** Excellent, flawless performance. */
    S(threshold = 100.0),

    /** Very good performance. */
    A(threshold = 90.0),

    /** Mediocre performance. */
    B(threshold = 75.0),

    /** Poor, but sufficient performance. */
    C(threshold = 50.0),

    /** Fail. */
    D(threshold = 0.0),

    /** Not assigned. */
    NA(threshold = -1.0);

    companion object {
        fun fromPercentage(percentage: Percentage): GameGrade {
            return with(percentage.value) {
                when {
                    this >= S.threshold -> S
                    this >= A.threshold -> A
                    this >= B.threshold -> B
                    this >= C.threshold -> C
                    else -> D
                }
            }
        }
    }
}