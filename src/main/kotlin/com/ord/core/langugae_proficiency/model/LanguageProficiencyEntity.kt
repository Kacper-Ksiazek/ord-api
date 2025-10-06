package com.ord.core.langugae_proficiency.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.user.model.UserEntity
import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("language_proficiencies")
data class LanguageProficiencyEntity(
    @Id
    override var id: UUID? = null,

    val language: LanguageName,
    var level: LanguageProficiencyLevel,
    var generativeContentLanguage: LanguageName,

    override val userId: UUID,

    val createdAt: Instant = Instant.now(),
) : IdentifiableUserResource