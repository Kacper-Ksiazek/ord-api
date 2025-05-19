package com.backend.ord.core.word.model

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.core.word.model.json.ExampleSentence
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.shared.models.IdentifiableUserResource
import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "words")
data class WordEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @Column(name = "origin", nullable = false)
    var origin: String,

    @Column(name = "translation", nullable = false)
    var translation: String,

    @Column(name = "definition", nullable = false)
    var definition: String,

    @Column(name = "use_cases", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    var useCases: Set<String> = emptySet(),

    @Column(name = "is_completed", nullable = false)
    var isCompleted: Boolean = false,

    @Column(name = "is_bookmarked", nullable = false)
    var isBookmarked: Boolean = false,

    @Column(name = "points", nullable = false)
    var points: Int = 0,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    var type: WordType,

    @Column(name = "extra_mark", nullable = false)
    @Enumerated(EnumType.STRING)
    var extraMark: WordExtraMark? = null,

    @Column(name = "translated_from", nullable = false)
    @Enumerated(EnumType.STRING)
    var translatedFrom: LanguageName,

    @Column(name = "translated_to", columnDefinition = "language_name(0, 0) not null")
    @Enumerated(EnumType.STRING)
    var translatedTo: LanguageName,

    @Column(name = "example_sentences", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    var exampleSentences: Set<ExampleSentence> = emptySet(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    override var user: UserEntity,

    @Column(name = "user_id", insertable = false, updatable = false)
    var userId: UUID = user.id,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "bank_id", nullable = true)
    var bank: Bank? = null,

    @Column(name = "bank_id", nullable = true, insertable = false, updatable = false)
    var bankId: UUID? = bank?.id,

    @Column(name = "bank_group_id", nullable = true, updatable = false)
    var bankGroupId: UUID? = bank?.bankGroupId,

    @Column(name = "completed_at", nullable = true)
    var completedAt: Instant? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false, updatable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource {
    @PostLoad
    fun populateUserId() {
        userId = user.id
        bankId = bank?.id
        bankGroupId = bank?.bankGroupId
    }
}