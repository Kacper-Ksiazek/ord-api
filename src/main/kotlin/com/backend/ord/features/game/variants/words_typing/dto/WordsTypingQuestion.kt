package com.backend.ord.features.game.variants.words_typing.dto

import java.util.*

data class WordsTypingQuestion(
    val id: UUID,
    val word: String,
    val clue: String,
)