package com.ord.features.quickly_added_words.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("quickly_added_words")
data class QuicklyAddedWordEntity(
    @Id
    override var id: UUID? = null,

    var word: String,
    var language: LanguageName,

    var translation: String? = null,
    var definition: String? = null,
    var extraMark: WordExtraMark? = null,
    var type: WordType? = null,

    var isApproved: Boolean = false,

    var createdAt: Instant = Instant.now(),

    override val userId: UUID,
) : IdentifiableUserResource