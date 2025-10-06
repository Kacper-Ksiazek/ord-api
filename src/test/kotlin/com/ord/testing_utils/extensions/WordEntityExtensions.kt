package com.ord.testing_utils.extensions

import com.ord.core.word.api.requests.dto.UpdateWordRequest
import com.ord.core.word.model.WordEntity
import io.kotest.matchers.shouldBe

//fun WordEntity.compareWith(anotherEntity: WordEntity) {
//    origin shouldBe anotherEntity.origin
//    translatedTo shouldBe anotherEntity.translatedTo
//    translatedFrom shouldBe anotherEntity.translatedFrom
//    type shouldBe anotherEntity.type
//    exampleSentences shouldBe anotherEntity.exampleSentences
//    translation shouldBe anotherEntity.translation
//    extraMark shouldBe anotherEntity.extraMark
//    definition shouldBe anotherEntity.definition
//    useCases shouldBe anotherEntity.useCases
//}
//
//fun WordEntity.detectChanges(
//    before: WordEntity,
//    changes: WordDataChanges = WordDataChanges()
//) {
//    detectChanges(
//        changes = changes,
//        before = UpdateWordRequest(
//            origin = before.origin,
//            translation = before.translation,
//            definition = before.definition,
//
//            type = before.type,
//            extraMark = before.extraMark,
//            translatedTo = before.translatedTo,
//            translatedFrom = before.translatedFrom,
//
//            useCases = before.useCases,
//            exampleSentences = before.exampleSentences,
//        )
//    )
//}
//
//fun WordEntity.detectChanges(
//    before: UpdateWordRequest,
//    changes: WordDataChanges = WordDataChanges()
//) {
//    origin shouldBe changes.origin.getOrDefault(before.origin)
//    definition shouldBe changes.definition.getOrDefault(before.definition)
//    translation shouldBe changes.translation.getOrDefault(before.translation)
//
//    type shouldBe changes.type.getOrDefault(before.type)
//    extraMark shouldBe changes.extraMark.getOrDefault(before.extraMark)
//    translatedTo shouldBe changes.translatedTo.getOrDefault(before.translatedTo)
//    translatedFrom shouldBe changes.translatedFrom.getOrDefault(before.translatedFrom)
//
//    useCases shouldBe changes.useCases.getOrDefault(before.useCases)
//    exampleSentences shouldBe changes.exampleSentences.getOrDefault(before.exampleSentences)
//}