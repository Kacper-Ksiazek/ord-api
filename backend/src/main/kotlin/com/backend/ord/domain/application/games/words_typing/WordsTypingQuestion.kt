package com.backend.ord.domain.application.games.words_typing

import java.util.*

data class WordsTypingQuestion(
    val id: UUID,
    val word: String,
    val clue: String,
)
