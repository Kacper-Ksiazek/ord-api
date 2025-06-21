package com.ord.core.word.api.responses.dto

import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence

data class AIGeneratedWordManual(
    // TODO: Rename this to `word`
    var originalWord: String = "",
    val translation: String,
    val definition: String,

    val type: WordType,
    val extraMark: WordExtraMark? = null,

    var useCases: Set<String> = emptySet(),
    val exampleSentences: Set<ExampleSentence>
)