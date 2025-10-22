package com.ord.core.word.models.word_details.jsonb

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ExampleSentence(
    @field:Size(min = 1, max = 128, message = "Context must be between 1 and 128 characters")
    var context: String? = null,

    @field:NotBlank(message = "Sentence cannot be blank")
    @field:Size(min = 1, max = 512, message = "Sentence must be between 1 and 255 characters")
    var sentence: String,

    @field:NotBlank(message = "Translation cannot be blank")
    @field:Size(min = 1, max = 512, message = "Translation must be between 1 and 255 characters")
    var translation: String
)