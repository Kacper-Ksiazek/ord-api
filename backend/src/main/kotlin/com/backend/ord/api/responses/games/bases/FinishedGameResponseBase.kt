package com.backend.ord.api.responses.games.bases

import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.utils.data_classes.Percentage

open class FinishedGameResponseBase(
    val finalScore: Double,
) {
    private val _finalScore: Percentage
        get() = Percentage(finalScore)

    val grade: GameGrade
        get() = GameGrade.fromPercentage(_finalScore)

    constructor(totalPoints: Int) : this(
        finalScore = Percentage(totalPoints).value,
    )
}

