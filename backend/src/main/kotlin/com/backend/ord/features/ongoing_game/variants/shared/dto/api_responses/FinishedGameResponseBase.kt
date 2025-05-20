package com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses

import com.backend.ord.features.ongoing_game.model.enums.GameGrade
import com.backend.ord.utils.data_classes.Percentage

abstract class FinishedGameResponseBase(
    val finalScore: Double,
) {
    private val _finalScore: Percentage
        get() = Percentage(finalScore)

    val grade: GameGrade
        get() = GameGrade.Companion.fromPercentage(_finalScore)

    constructor(totalPoints: Int) : this(
        finalScore = Percentage(totalPoints).value,
    )
}