package com.backend.ord.domain.entities

import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordType
import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "words")
class Word(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "origin", nullable = false)
    var origin: String,

    @Column(name = "translation", nullable = false)
    var translation: String,

    @Column(name = "is_bookmarked", nullable = false)
    var isBookmarked: Boolean = false,

    @Column(name = "points", nullable = false)
    var points: Int = 0,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    var type: WordType,

    @Column(name = "translated_from", nullable = false)
    @Enumerated(EnumType.STRING)
    var translatedFrom: LanguageName,

    @Column(name = "translated_to", columnDefinition = "language_name(0, 0) not null")
    @Enumerated(EnumType.STRING)
    var translatedTo: LanguageName,

    @Column(name = "example_sentences", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    var exampleSentences: MutableSet<ExampleSentence> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "bank_id", nullable = true)
    var bank: Bank? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
)