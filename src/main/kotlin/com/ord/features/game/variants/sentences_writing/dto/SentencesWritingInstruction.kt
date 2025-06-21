package com.ord.features.game.variants.sentences_writing.dto

import com.ord.features.game.variants.sentences_writing.dto.helpers.SentencesWritingTopic
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
