package com.ord.core.word.models.word_details.jsonb

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class WordConjugation(
    @field:NotBlank(message = "Tense cannot be blank")
    @field:Size(min = 1, max = 32, message = "Tense must be between 1 and 32 characters")
    var tense: String,

    @field:NotNull(message = "Forms cannot be null")
    var forms: Map<String, String>
)