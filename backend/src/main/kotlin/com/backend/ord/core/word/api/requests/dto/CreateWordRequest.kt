package com.backend.ord.core.word.api.requests.dto

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.core.word.model.json.ExampleSentence
import com.backend.ord.features.bank.api.requests.dto.CreateBankRequest
import com.backend.ord.validators.annotations.ValidStringSet
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class CreateWordRequest(
    @field:NotBlank(message = "Origin word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Origin word must be between 1 and 255 characters")
    val origin: String,

    @field:NotBlank(message = "Translation word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Translation word must be between 1 and 255 characters")
    val translation: String,

    @field:NotBlank(message = "Definition cannot be blank")
    @field:Size(min = 1, max = 255, message = "Definition must be between 1 and 255 characters")
    val definition: String,

    @field:ValidStringSet(
        message = "Use cases are invalid",
        minSetSize = 1,
        maxSetSize = 5,
        minElementSize = 5,
        maxElementSize = 96
    )
    val useCases: Set<String> = emptySet(),

    @field:NotNull(message = "Type cannot be blank")
    val type: WordType,

    @field:NotNull(message = "Translated from cannot be blank")
    val translatedFrom: LanguageName,

    val extraMark: WordExtraMark? = null,
    val translatedTo: LanguageName? = null,

    @field:Size(min = 1, max = 5, message = "Example sentences must be between 1 and 5")
    @field:Valid
    val exampleSentences: Set<ExampleSentence>,

    val bankId: UUID? = null,
    val bankToCreate: CreateBankRequest? = null
)