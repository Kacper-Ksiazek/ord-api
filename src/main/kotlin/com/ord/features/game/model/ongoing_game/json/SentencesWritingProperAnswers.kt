package com.ord.features.game.model.ongoing_game.json

import com.ord.features.game.variants.sentences_writing.dto.SentencesWritingInstruction
import java.util.*

/**
 * There are no proper answers in this case, because it requires user to write a full
 * sentences, therefore the only things which are stored in the DB are topics and their words.
 */
data class SentencesWritingSingleTopicProperAnswer(
    val id: UUID,
    val word: String,
    val topic: String
)

typealias SentencesWritingProperAnswers = Set<SentencesWritingSingleTopicProperAnswer>

fun SentencesWritingProperAnswers(instruction: SentencesWritingInstruction): SentencesWritingProperAnswers {
    return instruction.map {
        SentencesWritingSingleTopicProperAnswer(
            id = it.id,
            word = it.word,
            topic = it.topic
        )
    }.toSet()
}
