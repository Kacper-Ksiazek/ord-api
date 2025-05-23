package com.backend.ord.features.game.model.json

import java.util.*

data class SentencesWritingSingleTopicProperAnswer(
    val id: UUID,
    val topic: String,
)

typealias SentencesWritingProperAnswers = Set<SentencesWritingSingleTopicProperAnswer>
