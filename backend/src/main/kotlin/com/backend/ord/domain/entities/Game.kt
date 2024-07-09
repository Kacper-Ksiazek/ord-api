package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.abstracts.EntityBase
import com.backend.ord.enums.Game.GameStatus
import com.backend.ord.enums.Game.GameType
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "games")
class Game(
    @Column(name = "final_score", nullable = false)
    var finalScore: Int = 0,

    @Column(name = "acquired_points", nullable = false)
    var acquiredPoints: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "game_type(0, 0) not null", nullable = false)
    var type: GameType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "game_status(0, 0) not null", nullable = false)
    var status: GameStatus = GameStatus.IN_PROGRESS,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
) : EntityBase()