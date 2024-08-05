package com.backend.ord.api.requests.word

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class CreateWordRequest(
    @field:NotBlank(message = "Origin word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Origin word must be between 1 and 255 characters")
    var origin: String,

    @field:NotBlank(message = "Translation word cannot be blank")
    @field:Size(min = 1, max = 255, message = "Translation word must be between 1 and 255 characters")
    var translation: String,

    @field:NotBlank(message = "Type cannot be blank")
    var type: WordType,

    @field:NotBlank(message = "Translated from cannot be blank")
    var translatedFrom: LanguageName,

    var extraMark: WordExtraMark? = null,

    // By default, it will be translated to the native language of the user
    var translatedTo: LanguageName? = null,

    var exampleSentences: Set<ExampleSentence>,

    var bankId: UUID? = null,

    var bankToCreate: CreateBankRequest? = null
)