package com.backend.ord.controllers.helpers.request_factories.data

import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.controllers.helpers.extensions.detectChanges
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.domain.persistence.jsons.ExampleSentence
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType

val CreateWordData = UpdateWordRequestData(
    origin = "word in english",
    translation = "slowo po polsku",
    definition = "definition",

    type = WordType.NOUN,
    extraMark = WordExtraMark.SLANG,
    translatedTo = LanguageName.POLISH,
    translatedFrom = LanguageName.ENGLISH,

    useCases = setOf("use case 1", "use case 2"),
    exampleSentences = setOf(
        ExampleSentence(
            sentence = "example sentence",
            translation = "przykladowe zdanie"
        ),
        ExampleSentence(
            sentence = "another example sentence",
            translation = "kolejne przykladowe zdanie"
        )
    ),

    bankId = null,
    bankToCreate = null
)

fun Word.compareWithDefaultCreateWordData(
    differences: WordDataChanges = WordDataChanges()
) {
    detectChanges(
        before = CreateWordData,
        changes = differences
    )
}
