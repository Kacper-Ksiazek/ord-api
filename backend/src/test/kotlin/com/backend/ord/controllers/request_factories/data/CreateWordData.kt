package com.backend.ord.controllers.request_factories.data

import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.controllers.extensions.detectChanges
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.word.WordExtraMark
import com.backend.ord.enums.word.WordType

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
