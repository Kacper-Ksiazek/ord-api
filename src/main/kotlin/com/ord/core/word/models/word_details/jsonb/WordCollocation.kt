package com.ord.core.word.models.word_details.jsonb

import com.ord.core.word.models.word_details.enums.WordCollocationFrequency
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class WordCollocation(
    @field:NotBlank(message = "Phrase cannot be blank")
    @field:Size(min = 1, max = 128, message = "Phrase must be between 1 and 128 characters")
    var phrase: String,

    @field:NotBlank(message = "Translation cannot be blank")
    @field:Size(min = 1, max = 128, message = "Translation must be between 1 and 128 characters")
    var translation: String,

    @field:NotNull(message = "Frequency cannot be null")
    var frequency: WordCollocationFrequency
)