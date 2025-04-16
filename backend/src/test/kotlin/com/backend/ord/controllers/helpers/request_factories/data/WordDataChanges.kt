package com.backend.ord.controllers.helpers.request_factories.data

import com.backend.ord.domain.persistence.jsons.ExampleSentence
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType
import com.backend.ord.utils.Optional

data class WordDataChanges(
    val origin: Optional<String?> = Optional(null),
    val translation: Optional<String?> = Optional(null),
    val definition: Optional<String?> = Optional(null),

    val type: Optional<WordType?> = Optional(null),
    val translatedFrom: Optional<LanguageName?> = Optional(null),
    val extraMark: Optional<WordExtraMark?> = Optional(null),
    val translatedTo: Optional<LanguageName?> = Optional(null),

    val useCases: Optional<Set<String>?> = Optional(null),
    val exampleSentences: Optional<Set<ExampleSentence>?> = Optional(null)
)