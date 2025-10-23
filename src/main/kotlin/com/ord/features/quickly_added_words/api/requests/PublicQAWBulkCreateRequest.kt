package com.ord.features.quickly_added_words.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType

data class PublicQAWWordItem(
    val word: String,
    val definition: String? = null,
    val extraMark: WordExtraMark? = null,
    val type: WordType? = null,
)

data class PublicQAWBulkCreateRequest(
    val userEmail: String,
    val words: List<PublicQAWWordItem>,
    val language: LanguageName,
)