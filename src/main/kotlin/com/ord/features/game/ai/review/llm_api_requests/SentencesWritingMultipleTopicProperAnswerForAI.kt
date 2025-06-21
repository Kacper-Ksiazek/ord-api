package com.ord.features.game.ai.review.llm_api_requests

/**
 * This data class is being used to construct review requests to OpenAI
 */
data class SentencesWritingMultipleTopicProperAnswerForAI(
    val word: String,
    val topic: String,
    val answer: String
)