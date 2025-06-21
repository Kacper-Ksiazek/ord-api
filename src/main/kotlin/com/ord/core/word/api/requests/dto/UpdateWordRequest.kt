package com.ord.core.word.api.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.shared.validators.annotations.ValidStringSet
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.util.*

data class UpdateWordRequest(
    @field:Size(min = 1, max = 255, message = "Origin word must be between 1 and 255 characters")
    val origin: String? = null,

    @field:Size(min = 1, max = 255, message = "Translation word must be between 1 and 255 characters")
    val translation: String? = null,

    @field:Size(min = 1, max = 255, message = "Definition must be between 1 and 255 characters")
    val definition: String? = null,

    @field:ValidStringSet(
        message = "Use cases are invalid",
        minSetSize = 1,
        maxSetSize = 5,
        minElementSize = 5,
        maxElementSize = 96
    )
    val useCases: Set<String>? = null,

    val type: WordType? = null,

    val translatedFrom: LanguageName? = null,

    val extraMark: WordExtraMark? = null,

    val translatedTo: LanguageName? = null,

    @field:Size(min = 1, max = 5, message = "Example sentences must be between 1 and 5")
    @field:Valid
    val exampleSentences: Set<ExampleSentence>? = null,

    val bankId: UUID? = null,

    @field:Valid
    val bankToCreate: CreateBankRequest? = null
)