package com.ord.testing_utils.mocks.games.sentences_writing

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameAnswers
import java.io.File
import java.util.UUID

class SentencesWritingAnswersMockLoader(
    private val filePath: String = "src/test/resources/mocks/games/sentences_writing_answers.json"
) {
    private val objectMapper = ObjectMapper()

    fun mockAnswers(difficulty: GameDifficulty): FinishSentencesWritingGameAnswers {
        val file = File(filePath)
        val allAnswers: Map<String, Map<String, String>> = objectMapper.readValue(
            file,
            object : TypeReference<Map<String, Map<String, String>>>() {}
        )
        val answersForDifficulty = allAnswers[difficulty.name]
            ?: throw IllegalArgumentException("No answers found for difficulty: ${difficulty.name}")

        return answersForDifficulty.mapKeys { UUID.fromString(it.key) }
    }
}