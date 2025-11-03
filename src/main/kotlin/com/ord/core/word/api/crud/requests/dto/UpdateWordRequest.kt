package com.ord.core.word.api.crud.requests.dto

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.util.*

data class UpdateWordRequest(
    val type: WordType? = null,

    @field:Size(min = 1, max = 255, message = "Source word must be between 1 and 255 characters")
    val sourceWord: String? = null,

    @field:Size(min = 1, max = 255, message = "Translation word must be between 1 and 255 characters")
    val translation: String? = null,

    @field:Size(min = 1, max = 255, message = "Definition must be between 1 and 255 characters")
    val definition: String? = null,

    val extraMark: WordExtraMark? = null,

    @field:ValidLanguageName
    val language: LanguageName? = null,

    val bankId: UUID? = null,

    @field:Valid
    val bankToCreate: CreateBankRequest? = null
)