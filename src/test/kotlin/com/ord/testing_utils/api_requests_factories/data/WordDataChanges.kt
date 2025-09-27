package com.ord.testing_utils.api_requests_factories.data

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.shared.utils.data_classes.NonRequired

data class WordDataChanges(
    val origin: NonRequired<String?> = NonRequired(null),
    val translation: NonRequired<String?> = NonRequired(null),
    val definition: NonRequired<String?> = NonRequired(null),

    val type: NonRequired<WordType?> = NonRequired(null),
    val translatedFrom: NonRequired<LanguageName?> = NonRequired(null),
    val extraMark: NonRequired<WordExtraMark?> = NonRequired(null),
    val translatedTo: NonRequired<LanguageName?> = NonRequired(null),

    val useCases: NonRequired<String?> = NonRequired(null),
    val exampleSentences: NonRequired<String?> = NonRequired(null)
)