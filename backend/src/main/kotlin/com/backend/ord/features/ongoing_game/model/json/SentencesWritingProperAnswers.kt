package com.backend.ord.features.ongoing_game.model.json

import java.util.*

data class SentencesWritingSingleTopicProperAnswer(
    val id: UUID,
    val topic: String,
)

typealias SentencesWritingProperAnswers = Set<SentencesWritingSingleTopicProperAnswer>
