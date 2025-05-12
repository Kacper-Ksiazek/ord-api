package com.backend.ord.domain.application.games

import java.util.*

data class SentencesWritingInstruction(
    val id: UUID,
    val word: String,
    val topic: String,
)

fun Map<String, String>.convertToSentencesWritingInstruction(): List<SentencesWritingInstruction> {
    return this.map { (word, topic) ->
        SentencesWritingInstruction(
            id = UUID.randomUUID(),
            word = word,
            topic = topic
        )
    }
