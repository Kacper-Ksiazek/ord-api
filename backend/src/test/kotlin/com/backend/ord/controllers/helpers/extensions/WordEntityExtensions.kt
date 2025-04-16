package com.backend.ord.controllers.helpers.extensions

import com.backend.ord.api.requests.word.UpdateWordRequest
import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.controllers.helpers.request_factories.data.WordDataChanges
import com.backend.ord.domain.persistence.entities.Word
import io.kotest.matchers.shouldBe

fun Word.compareWith(anotherEntity: Word) {
    origin shouldBe anotherEntity.origin
    translatedTo shouldBe anotherEntity.translatedTo
    translatedFrom shouldBe anotherEntity.translatedFrom
    type shouldBe anotherEntity.type
    exampleSentences shouldBe anotherEntity.exampleSentences
    translation shouldBe anotherEntity.translation
    extraMark shouldBe anotherEntity.extraMark
    definition shouldBe anotherEntity.definition
    useCases shouldBe anotherEntity.useCases
}

fun Word.detectChanges(
    before: Word,
    changes: WordDataChanges = WordDataChanges()
) {
    detectChanges(
        changes = changes,
        before = UpdateWordRequestData(
            origin = before.origin,
            translation = before.translation,
            definition = before.definition,

            type = before.type,
            extraMark = before.extraMark,
            translatedTo = before.translatedTo,
            translatedFrom = before.translatedFrom,

            useCases = before.useCases,
            exampleSentences = before.exampleSentences,
        )
    )
}

fun Word.detectChanges(
    before: UpdateWordRequest,
    changes: WordDataChanges = WordDataChanges()
) {
    origin shouldBe changes.origin.getOrDefault(before.origin)
    definition shouldBe changes.definition.getOrDefault(before.definition)
    translation shouldBe changes.translation.getOrDefault(before.translation)

    type shouldBe changes.type.getOrDefault(before.type)
    extraMark shouldBe changes.extraMark.getOrDefault(before.extraMark)
    translatedTo shouldBe changes.translatedTo.getOrDefault(before.translatedTo)
    translatedFrom shouldBe changes.translatedFrom.getOrDefault(before.translatedFrom)

    useCases shouldBe changes.useCases.getOrDefault(before.useCases)
    exampleSentences shouldBe changes.exampleSentences.getOrDefault(before.exampleSentences)
}