package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.api.requests.word.UpdateWordRequest
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.backend.ord.validators.annotations.ValidStringSet
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class UpdateWordRequestData(
    @field:NotNull(message = "Id cannot be blank")
    override val id: UUID,

    @field:NotBlank(message = "Origin word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Origin word must be between 1 and 255 characters")
    override val origin: String,

    @field:NotBlank(message = "Translation word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Translation word must be between 1 and 255 characters")
    override val translation: String,

    @field:NotBlank(message = "Definition cannot be blank")
    @field:Size(min = 1, max = 255, message = "Definition must be between 1 and 255 characters")
    override val definition: String,

    @field:ValidStringSet(
        message = "Use cases are invalid",
        minSetSize = 1,
        maxSetSize = 5,
        minElementSize = 5,
        maxElementSize = 96
    )
    override val useCases: Set<String> = emptySet(),

    @field:NotNull(message = "Type cannot be blank")
    override val type: WordType,

    @field:NotNull(message = "Translated from cannot be blank")
    override val translatedFrom: LanguageName,

    override val extraMark: WordExtraMark? = null,

    override val translatedTo: LanguageName? = null,

    @field:Size(min = 1, max = 5, message = "Example sentences must be between 1 and 5")
    @field:Valid
    override val exampleSentences: Set<ExampleSentence>,

    override val bankId: UUID? = null,

    override val bankToCreate: CreateBankRequest? = null
) : UpdateWordRequest