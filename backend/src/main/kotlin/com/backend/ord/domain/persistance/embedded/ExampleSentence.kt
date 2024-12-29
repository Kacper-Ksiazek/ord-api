package com.backend.ord.domain.persistance.embedded

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ExampleSentence(
    @field:NotBlank(message = "Sentence cannot be blank")
    @field:Size(min = 1, max = 255, message = "Sentence must be between 1 and 255 characters")
    var sentence: String,

    @field:NotBlank(message = "Translation cannot be blank")
    @field:Size(min = 1, max = 255, message = "Translation must be between 1 and 255 characters")
    var translation: String
)
