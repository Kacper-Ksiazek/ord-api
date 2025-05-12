package com.backend.ord.domain.persistence.jsons.game_proper_answers

import java.util.*

data class SentencesWritingSingleTopicProperAnswer(
    val id: UUID,
    val topic: String,
)

/**
 * This data class is being used to construct review requests to OpenAI
 */
data class SentencesWritingMultipleTopicProperAnswerForAI(
    val word: String,
    val topic: String,
)

/**
 * This type in fact is the one which is being serialized and further stored in DB
 */
typealias SentencesWritingProperAnswers = Set<SentencesWritingSingleTopicProperAnswer>
