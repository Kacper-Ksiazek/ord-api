package com.backend.ord.domain.application.games

import java.util.*

data class SentencesWritingSingleInstruction(
    val id: UUID,
    val word: String,
    val topic: String,
)

typealias SentencesWritingInstruction = List<SentencesWritingSingleInstruction>

fun Map<String, String>.convertToSentencesWritingInstruction(): List<SentencesWritingSingleInstruction> {
    return this.map { (word, topic) ->
        SentencesWritingSingleInstruction(
            id = UUID.randomUUID(),
            word = word,
            topic = topic
        )
    }
}
