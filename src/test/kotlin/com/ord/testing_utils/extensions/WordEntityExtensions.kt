package com.ord.testing_utils.extensions

//fun WordEntity.compareWith(anotherEntity: WordEntity) {
//    origin shouldBe anotherEntity.sourceWord
//    translatedTo shouldBe anotherEntity.language
//    translatedFrom shouldBe anotherEntity.language
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
//            origin = before.sourceWord,
//            translation = before.translation,
//            definition = before.definition,
//
//            type = before.type,
//            extraMark = before.extraMark,
//            translatedTo = before.language,
//            translatedFrom = before.language,
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
//    origin shouldBe changes.sourceWord.getOrDefault(before.sourceWord)
//    definition shouldBe changes.definition.getOrDefault(before.definition)
//    translation shouldBe changes.translation.getOrDefault(before.translation)
//
//    type shouldBe changes.type.getOrDefault(before.type)
//    extraMark shouldBe changes.extraMark.getOrDefault(before.extraMark)
//    translatedTo shouldBe changes.language.getOrDefault(before.language)
//    translatedFrom shouldBe changes.language.getOrDefault(before.language)
//
//    useCases shouldBe changes.useCases.getOrDefault(before.useCases)
//    exampleSentences shouldBe changes.exampleSentences.getOrDefault(before.exampleSentences)
//}