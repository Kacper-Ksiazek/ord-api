package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.word.UpdateWordRequest
import com.backend.ord.domain.persistence.jsons.ExampleSentence
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType
import com.backend.ord.validators.annotations.ValidStringSet
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.util.*

data class UpdateWordRequestData(
    @field:Size(min = 1, max = 255, message = "Origin word must be between 1 and 255 characters")
    override val origin: String? = null,

    @field:Size(min = 1, max = 255, message = "Translation word must be between 1 and 255 characters")
    override val translation: String? = null,

    @field:Size(min = 1, max = 255, message = "Definition must be between 1 and 255 characters")
    override val definition: String? = null,

    @field:ValidStringSet(
        message = "Use cases are invalid",
        minSetSize = 1,
        maxSetSize = 5,
        minElementSize = 5,
        maxElementSize = 96
    )
    override val useCases: Set<String>? = null,

    override val type: WordType? = null,

    override val translatedFrom: LanguageName? = null,

    override val extraMark: WordExtraMark? = null,

    override val translatedTo: LanguageName? = null,

    @field:Size(min = 1, max = 5, message = "Example sentences must be between 1 and 5")
    @field:Valid
    override val exampleSentences: Set<ExampleSentence>? = null,

    override val bankId: UUID? = null,

    @field:Valid
    override val bankToCreate: CreateBankRequestData? = null
) : UpdateWordRequest