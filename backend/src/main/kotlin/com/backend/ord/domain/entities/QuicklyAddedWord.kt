package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.abstracts.EntityBase
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Language.LanguageProficiencyLevel
import jakarta.persistence.*

@Entity
@Table(name = "quickly_added_words")
class QuicklyAddedWord(
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    var language: LanguageName,

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency", nullable = false)
    var proficiency: LanguageProficiencyLevel,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User
) : EntityBase()