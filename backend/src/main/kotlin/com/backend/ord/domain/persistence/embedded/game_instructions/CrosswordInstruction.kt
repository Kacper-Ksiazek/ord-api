package com.backend.ord.domain.persistence.embedded.game_instructions

import com.backend.ord.domain.application.games.Board
import com.backend.ord.domain.application.games.Coordinates
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.services.ai.dto.crossword.CrosswordQuestion
import java.util.*

class CrosswordInstruction {
    val answerExplanation: String;
    val answer: String;
    val questions: Set<CrosswordQuestion>;
    val board: List<List<String?>>

    constructor(
        aiGeneratedQuestions: AIGeneratedCrossword,
        boardDimension: Coordinates = Coordinates(x = 32, y = 24),
        firstWordStart: Coordinates = Coordinates(x = 5, y = 5)
    ) {
        val board = Board(boardDimension)

        this.answerExplanation = aiGeneratedQuestions.answerExplanation
        this.answer = aiGeneratedQuestions.answer
        this.questions = board.placeAllQuestions(
            questions = aiGeneratedQuestions.questions,
            firstWordStart = firstWordStart
        )

        this.board = board.trim(this)
    }
}

// TODO: Move it somewhere
data class CrosswordGameProperAnswers(
    // Rename:
    // - finalWord -> answer
    // - properAnswers -> solution
    val finalWord: String,
    val questions: Map<UUID, String>
)