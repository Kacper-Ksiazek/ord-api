package com.backend.ord.domain.entities.gpt_tokens_usage

import com.backend.ord.domain.entities.Game
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.abstracts.EntityBase
import com.backend.ord.enums.TokensUsage.GamesGPTTokensConsumptionType
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "game_tokens_usages")
class GameTokensUsage(
    @field:Min(1)
    @Column(name = "number_of_generations", nullable = false)
    var numberOfGenerations: Int = 1,

    @field:Min(0)
    @Column(name = "number_of_tokens", nullable = false)
    var numberOfTokens: Int,

    @Column(name = "consumption_type", columnDefinition = "games_gpt_tokens_consumption_type(0, 0) not null")
    @Enumerated(EnumType.STRING)
    var consumptionType: GamesGPTTokensConsumptionType,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "game_id")
    var games: Game

) : EntityBase()