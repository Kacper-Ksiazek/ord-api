package com.ord.features.game.model.ongoing_game.json

import com.ord.features.game.variants.sentences_writing.dto.SentencesWritingInstruction
import java.util.*

data class SentencesWritingSingleTopicProperAnswer(
    val id: UUID,
    val topic: String,
)

/**
 * There are no proper answers in this case, because it requires user to write a full
 * sentences, therefore the only things which are stored in the DB are topics.
 */
typealias SentencesWritingProperAnswers = Set<SentencesWritingSingleTopicProperAnswer>

fun SentencesWritingProperAnswers(instruction: SentencesWritingInstruction): SentencesWritingProperAnswers {
    return instruction.map { SentencesWritingSingleTopicProperAnswer(it.id, it.topic) }.toSet()
}
