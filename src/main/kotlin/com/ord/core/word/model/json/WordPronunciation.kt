package com.ord.core.word.model.json

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class WordPronunciation(
    @field:NotBlank(message = "IPA cannot be blank")
    @field:Size(min = 1, max = 64, message = "IPA must be between 1 and 64 characters")
    var ipa: String,

    @field:Size(min = 1, max = 64, message = "Syllables must be between 1 and 64 characters")
    var syllables: String? = null,

    @field:Min(1, message = "Stress must be at least 1")
    @field:Max(20, message = "Stress must be at most 20")
    var stress: Int? = null
)