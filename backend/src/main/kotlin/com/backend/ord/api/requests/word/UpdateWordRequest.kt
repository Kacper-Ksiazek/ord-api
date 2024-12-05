package com.backend.ord.api.requests.word

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import java.util.*

interface UpdateWordRequest {
    val origin: String?
    val translation: String?
    val definition: String?

    val type: WordType?
    val extraMark: WordExtraMark?
    val translatedTo: LanguageName?
    val translatedFrom: LanguageName?

    val useCases: Set<String>?
    val exampleSentences: Set<ExampleSentence>?

    val bankId: UUID?
    val bankToCreate: CreateBankRequest?
}