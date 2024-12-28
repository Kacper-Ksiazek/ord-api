package com.backend.ord.enums.persistance.game

import com.backend.ord.utils.data_classes.Percentage

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

    /** "Not Assigned", default value for a game when it is not graded yet. */
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