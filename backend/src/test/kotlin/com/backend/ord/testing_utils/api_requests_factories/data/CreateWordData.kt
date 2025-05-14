package com.backend.ord.testing_utils.api_requests_factories.data

import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.domain.persistence.jsons.ExampleSentence
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType
import com.backend.ord.testing_utils.extensions.detectChanges

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
