package com.backend.ord.api.requests.word

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.core.word.model.json.ExampleSentence
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