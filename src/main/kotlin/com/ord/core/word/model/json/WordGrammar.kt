package com.ord.core.word.model.json

import com.ord.core.word.model.enums.WordGender
import jakarta.validation.Valid
import jakarta.validation.constraints.Size

data class WordGrammar(
    var gender: WordGender? = null,

    @field:Size(min = 1, max = 16, message = "Definite article must be between 1 and 16 characters")
    var definiteArticle: String? = null,

    @field:Size(min = 1, max = 64, message = "Plural form must be between 1 and 64 characters")
    var pluralForm: String? = null,

    @field:Size(min = 1, max = 64, message = "Comparative form must be between 1 and 64 characters")
    var comparativeForm: String? = null,

    @field:Size(min = 1, max = 64, message = "Superlative form must be between 1 and 64 characters")
    var superlativeForm: String? = null,

    var irregularForms: Map<String, String>? = null,

    @field:Valid
    var conjugations: List<WordConjugation>? = null
)