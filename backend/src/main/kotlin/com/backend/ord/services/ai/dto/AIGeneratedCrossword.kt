package com.backend.ord.services.ai.dto

import com.backend.ord.domain.application.games.Coordinates
import com.backend.ord.domain.persistence.embedded.game_instructions.AnswerComponent
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordWordDirection
import java.util.*

data class QuestionBoardPosition(
    val coordinates: Coordinates,
    val direction: CrosswordWordDirection
)

data class AIGeneratedCrosswordQuestion(
    val id: UUID = UUID.randomUUID(),

    var word: String,
    val clue: String,

    var answerComponents: MutableList<AnswerComponent>? = null

    // Make nullable position field:
    //     val direction: CrosswordWordDirection,
    //
    //    val coordinates: WordPlacementRange,
)

data class AIGeneratedCrossword(
    var answer: String,
    val answerExplanation: String,
    val questions: List<AIGeneratedCrosswordQuestion>
)