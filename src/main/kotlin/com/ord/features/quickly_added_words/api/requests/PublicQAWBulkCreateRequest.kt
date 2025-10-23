package com.ord.features.quickly_added_words.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.api.annotations.validators.SafeString
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class PublicQAWWordItem(
    @field:SafeString(fieldName = "Word", min = 1, max = 255)
    val word: String,

    @field:Size(max = 2000, message = "Definition must not exceed 2000 characters")
    val definition: String? = null,

    val extraMark: WordExtraMark? = null,
    val type: WordType? = null,
)

data class PublicQAWBulkCreateRequest(
    @field:Email(message = "Invalid email address")
    @field:SafeString(fieldName = "Email")
    val userEmail: String,

    @field:NotEmpty(message = "Words list cannot be empty")
    @field:Size(min = 1, max = 100, message = "Words list must contain between 1 and 100 items")
    @field:Valid
    val words: List<PublicQAWWordItem>,

    val language: LanguageName,
)