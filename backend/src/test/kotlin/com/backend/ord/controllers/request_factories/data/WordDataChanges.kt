package com.backend.ord.controllers.request_factories.data

import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.enums.persistance.word.WordExtraMark
import com.backend.ord.enums.persistance.word.WordType
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