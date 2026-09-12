package com.ord.core.word.models.word

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("words")
data class WordEntity(
    @Id
    override val id: UUID? = null,

    var type: WordType,
    var sourceWord: String,
    var translation: String,
    var definition: String? = null,
    var extraMark: WordExtraMark? = null,

    var language: LanguageName,

    var isBookmarked: Boolean = false,

    override var userId: UUID,
    var bankId: UUID? = null,
    var bankGroupId: UUID? = null,

    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
) : IdentifiableUserResource {
    fun hasActivationFields(): Boolean = !definition.isNullOrBlank()
}
