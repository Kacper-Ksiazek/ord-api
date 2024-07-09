package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.abstracts.EntityBase
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
import jakarta.persistence.*

@Entity
@Table(name = "language_proficiencies")
class LanguageProficiency(
    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    var language: LanguageName,

    @Column(name = "proficiency", nullable = false)
    @Enumerated(EnumType.STRING)
    var proficiency: LanguageProficiencyLevel,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User
) : EntityBase()
