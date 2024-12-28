package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.interfaces.IdentifiableUserResource
import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.enums.persistance.language.LanguageProficiencyLevel
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "language_proficiencies")
class LanguageProficiency(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    var language: LanguageName,

    @Column(name = "proficiency", nullable = false)
    @Enumerated(EnumType.STRING)
    var proficiency: LanguageProficiencyLevel,

    @Column(name = "generative_content_language", nullable = false)
    @Enumerated(EnumType.STRING)
    var generativeContentLanguage: LanguageName,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    override var user: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource
