package com.ord.core.word.api.crud.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class CreateWordRequest(
    @field:NotNull(message = "Type cannot be blank")
    val type: WordType,

    @field:NotBlank(message = "Source word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Source word must be between 1 and 255 characters")
    val sourceWord: String,

    @field:NotBlank(message = "Translation word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Translation word must be between 1 and 255 characters")
    val translation: String,

    @field:NotBlank(message = "Definition cannot be blank")
    @field:Size(min = 1, max = 255, message = "Definition must be between 1 and 255 characters")
    val definition: String,

    val extraMark: WordExtraMark? = null,

    @field:NotNull(message = "Language cannot be blank")
    @field:ValidLanguageName
    val language: LanguageName,

    val bankId: UUID? = null,
    val bankToCreate: CreateBankRequest? = null
)