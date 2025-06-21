package com.ord.features.game.model.ongoing_game.json

import java.util.*

data class SentencesWritingSingleTopicProperAnswer(
    val id: UUID,
    val topic: String,
)

typealias SentencesWritingProperAnswers = Set<SentencesWritingSingleTopicProperAnswer>
