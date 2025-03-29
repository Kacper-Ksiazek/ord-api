package com.backend.ord.domain.persistence.entities.gpt_tokens_usage

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.interfaces.IdentifiableUserResource
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "word_tokens_usages")
data class WordTokensUsage(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override val id: UUID = UUID.randomUUID(),

    @Column(name = "word", nullable = false)
    val word: String,

    @Column(name = "translated_to", nullable = false)
    @Enumerated(EnumType.STRING)
    val translatedTo: LanguageName,

    @Column(name = "translated_from", nullable = false)
    @Enumerated(EnumType.STRING)
    val translatedFrom: LanguageName,

    @Column(name = "input_tokens", nullable = false)
    val inputTokens: Int,

    @Column(name = "output_tokens", nullable = false)
    val outputTokens: Int,

    @Column(name = "price_for_mln_input_tokens", nullable = false)
    val priceForMlnInputTokens: BigDecimal,

    @Column(name = "price_for_mln_output_tokens", nullable = false)
    val priceForMlnOutputTokens: BigDecimal,

    @Column(name = "cost", nullable = false)
    val cost: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "consumption_type", columnDefinition = "words_gpt_tokens_consumption_type(0, 0) not null")
    val consumptionType: WordsGPTTokensConsumptionType,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    override val user: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    val updatedAt: Instant = Instant.now()
) : IdentifiableUserResource