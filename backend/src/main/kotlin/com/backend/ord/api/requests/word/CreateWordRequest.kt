package com.backend.ord.api.requests.word

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.enums.word.WordExtraMark
import com.backend.ord.enums.word.WordType
import java.util.*

interface CreateWordRequest : UpdateWordRequest {
    override val origin: String
    override val translation: String
    override val definition: String

    override val type: WordType
    override val extraMark: WordExtraMark?
    override val translatedTo: LanguageName?
    override val translatedFrom: LanguageName

    override val useCases: Set<String>
    override val exampleSentences: Set<ExampleSentence>

    override val bankId: UUID?
    override val bankToCreate: CreateBankRequest?
}

