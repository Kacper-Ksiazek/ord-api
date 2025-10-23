package com.ord.features.quickly_added_words.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.api.annotations.validators.SafeString
import jakarta.validation.constraints.Size

data class CreateQAWRequest(
    @field:SafeString(fieldName = "Word", min = 1, max = 255)
    val word: String,

    val language: LanguageName,

    @field:Size(max = 2000, message = "Definition must not exceed 2000 characters")
    val definition: String? = null,

    val extraMark: WordExtraMark? = null,
    val type: WordType? = null,
)
