package com.backend.ord.api.requests.word

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.core.word.model.json.ExampleSentence
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

