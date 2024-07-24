package com.backend.ord.api.requests.words

import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordType
import java.util.*

data class CreateWordRequest(
    var origin: String,
    var translation: String,
    var type: WordType,
    var translatedFrom: LanguageName,

    // By default, it will be translated to the native language of the user
    var translatedTo: LanguageName? = null,

    var exampleSentences: Set<ExampleSentence>,
    var bankId: UUID? = null
)