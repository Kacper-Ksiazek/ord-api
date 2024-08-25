package com.backend.ord.domain.entities.gpt_tokens_usage

import com.backend.ord.domain.entities.User
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.TokensUsage.WordsGPTTokensConsumptionType
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "word_tokens_usages")
class WordTokensUsage(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

    @field:Min(0)
    @Column(name = "number_of_tokens", nullable = false)
    var numberOfTokens: Int,

    @Column(name = "word", nullable = false)
    var word: String,

    @Column(name = "translated_to", nullable = false)
    @Enumerated(EnumType.STRING)
    var translatedTo: LanguageName,

    @Column(name = "translated_from", nullable = false)
    @Enumerated(EnumType.STRING)
    var translatedFrom: LanguageName,

    @Enumerated(EnumType.STRING)
    @Column(name = "consumption_type", columnDefinition = "words_gpt_tokens_consumption_type(0, 0) not null")
    var consumptionType: WordsGPTTokensConsumptionType,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    var user: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
)