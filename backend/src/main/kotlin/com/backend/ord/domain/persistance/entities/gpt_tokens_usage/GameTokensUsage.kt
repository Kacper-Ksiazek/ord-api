package com.backend.ord.domain.persistance.entities.gpt_tokens_usage

import com.backend.ord.domain.persistance.entities.Game
import com.backend.ord.domain.persistance.entities.User
import com.backend.ord.domain.persistance.entities.interfaces.IdentifiableUserResource
import com.backend.ord.enums.persistance.game.GameDifficulty
import com.backend.ord.enums.persistance.game.GameType
import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.enums.persistance.tokens_usage.GamesGPTTokensConsumptionType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "game_tokens_usages")
class GameTokensUsage(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @Column(name = "input_tokens", nullable = false)
    val inputTokens: Int,

    @Column(name = "output_tokens", nullable = false)
    val outputTokens: Int,

    @Column(name = "price_for_mln_input_tokens", nullable = false)
    val priceForMlnInputTokens: BigDecimal,

    @Column(name = "price_for_mln_output_tokens", nullable = false)
    val priceForMlnOutputTokens: BigDecimal,

    @Column(name = "translated_from", nullable = false)
    @Enumerated(EnumType.STRING)
    val translatedFrom: LanguageName,

    @Column(name = "instruction_language", nullable = false)
    @Enumerated(EnumType.STRING)
    val instructionLanguage: LanguageName,

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", columnDefinition = "game_type(0, 0) not null", nullable = false)
    var gameType: GameType,

    @Enumerated(EnumType.STRING)
    @Column(name = "game_difficulty", columnDefinition = "game_difficulty(0, 0) not null", nullable = false)
    var gameDifficulty: GameDifficulty,

    @Column(name = "consumption_type", columnDefinition = "games_gpt_tokens_consumption_type(0, 0) not null")
    @Enumerated(EnumType.STRING)
    var consumptionType: GamesGPTTokensConsumptionType,

    @Column(name = "cost", nullable = false)
    val cost: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    override var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "game_id")
    var game: Game? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource