package com.backend.ord.domain.entities

import com.backend.ord.enums.Game.GameStatus
import com.backend.ord.enums.Game.GameType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "games")
class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "accuracy_rate", nullable = false)
    var accuracyRate: Int = 0,

    @Column(name = "acquired_points", nullable = false)
    var acquiredPoints: Int = 0,

    @Column(name = "duration", nullable = false)
    var duration: String = "00:00:00",

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "game_type(0, 0) not null", nullable = false)
    var type: GameType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "game_status(0, 0) not null", nullable = false)
    var status: GameStatus = GameStatus.IN_PROGRESS,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
)