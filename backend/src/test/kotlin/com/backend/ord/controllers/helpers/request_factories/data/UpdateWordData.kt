package com.backend.ord.controllers.helpers.request_factories.data

import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.controllers.helpers.extensions.detectChanges
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.domain.persistence.jsons.ExampleSentence
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType
import io.kotest.matchers.shouldBe
import java.util.*

val UpdateWordData = UpdateWordRequestData(
    origin = "UPDATED word in foraign language",
    translation = "UPDATED word in native language",
    definition = "UDPADED definition",

    type = WordType.VERB,
    extraMark = WordExtraMark.SLANG,
    translatedTo = LanguageName.SLOVENIAN,
    translatedFrom = LanguageName.NORWEGIAN,

    useCases = setOf("UPDATED use case 1", "UPDATED use case 2"),
    exampleSentences = setOf(
        ExampleSentence(
            sentence = "UPDATED example sentence",
            translation = "UPDATED"
        ),
        ExampleSentence(
            sentence = "another UPDATED example sentence",
            translation = "kolejne UPDATED przykladowe zdanie"
        )
    ),

    bankId = null,
    bankToCreate = null
)

fun Word.compareWithDefaultUpdateWordData(
    idOfWordToUpdate: UUID,
    differences: WordDataChanges = WordDataChanges()
) {
    id shouldBe idOfWordToUpdate

    detectChanges(
        before = UpdateWordData,
        changes = differences
    )
}
