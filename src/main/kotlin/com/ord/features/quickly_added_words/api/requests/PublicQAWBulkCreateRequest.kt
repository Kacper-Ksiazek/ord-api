package com.ord.features.quickly_added_words.api.requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName

data class PublicQAWBulkCreateRequest(
    val userEmail: String,
    val words: List<String>,
    val language: LanguageName,
)