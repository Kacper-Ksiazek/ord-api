package com.backend.ord.domain.persistence.jsons.game_proper_answers

import java.util.*

data class SentencesWritingSingleTopicProperAnswer(
    val id: UUID,
    val topic: String,
)

typealias SentencesWritingProperAnswers = Set<SentencesWritingSingleTopicProperAnswer>
