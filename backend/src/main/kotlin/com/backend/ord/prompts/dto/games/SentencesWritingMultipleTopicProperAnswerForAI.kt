package com.backend.ord.prompts.dto.games

/**
 * This data class is being used to construct review requests to OpenAI
 */
data class SentencesWritingMultipleTopicProperAnswerForAI(
    val word: String,
    val topic: String,
)
