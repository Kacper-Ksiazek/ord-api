package com.backend.ord.domain.entities.gpt_tokens_usage

import com.backend.ord.domain.entities.Game
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.interfaces.IdentifiableUserResource
import com.backend.ord.enums.tokens_usage.GamesGPTTokensConsumptionType
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "game_tokens_usages")
class GameTokensUsage(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @field:Min(0)
    @Column(name = "number_of_tokens", nullable = false)
    var numberOfTokens: Int,

    @Column(name = "consumption_type", columnDefinition = "games_gpt_tokens_consumption_type(0, 0) not null")
    @Enumerated(EnumType.STRING)
    var consumptionType: GamesGPTTokensConsumptionType,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    override var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "game_id")
    var game: Game,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource