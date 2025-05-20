package com.backend.ord.features.ongoing_game.variants.sentences_writing.dto

import com.backend.ord.features.ongoing_game.variants.sentences_writing.dto.helpers.SentencesWritingTopic
import java.util.*

typealias SentencesWritingInstruction = List<SentencesWritingTopic>

fun Map<String, String>.convertToSentencesWritingInstruction(): SentencesWritingInstruction {
    return this.map { (word, topic) ->
        SentencesWritingTopic(
            id = UUID.randomUUID(),
            word = word,
            topic = topic
        )
    }
}
