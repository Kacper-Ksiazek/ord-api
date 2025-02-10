package com.backend.ord.domain.persistence.embedded.game_instructions

import com.backend.ord.services.ai.dto.crossword.CrosswordQuestion
import java.util.*


data class CrosswordInstruction(
    val answerExplanation: String,
    val answer: String,
    val questions: Set<CrosswordQuestion>,
)

data class CrosswordGameProperAnswers(
    // Rename:
    // - finalWord -> answer
    // - properAnswers -> solution
    val finalWord: String,
    val questions: Map<UUID, String>
)