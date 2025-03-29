package com.backend.ord.seeders.mocks.words.json_data_models

import com.backend.ord.domain.persistence.embedded.ExampleSentence
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType

data class AIGeneratedWordManualInJSON(
    val originalWord: String,
    val translation: String,
    val definition: String,
    val type: WordType,
    val extraMark: WordExtraMark?,
    val useCases: List<String>,
    val exampleSentences: List<ExampleSentence>
)
