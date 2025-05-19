package com.backend.ord.core.word.api.responses.dto

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.core.word.model.json.ExampleSentence
import com.backend.ord.features.bank.dto.BankCompact
import java.time.Instant
import java.util.*

data class SingleWordResponse(
    val id: UUID,

    var points: Int,
    var origin: String,
    var definition: String,
    var translation: String,
    var isCompleted: Boolean,
    var isBookmarked: Boolean,

    var type: WordType,
    var extraMark: WordExtraMark?,
    var translatedTo: LanguageName,
    var translatedFrom: LanguageName,

    var useCases: Set<String>,
    var exampleSentences: Set<ExampleSentence>,

    var bank: BankCompact?,

    val createdAt: Instant,
    var updatedAt: Instant,
)