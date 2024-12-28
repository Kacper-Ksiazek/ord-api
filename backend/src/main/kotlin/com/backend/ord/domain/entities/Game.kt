package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.interfaces.IdentifiableUserResource
import com.backend.ord.enums.persistance.game.GameDifficulty
import com.backend.ord.enums.persistance.game.GameStatus
import com.backend.ord.enums.persistance.game.GameType
import com.backend.ord.enums.persistance.language.LanguageName
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
    override var id: UUID = UUID.randomUUID(),

    @Column(name = "final_score", nullable = false)
    var finalScore: Int = 0,

    @Column(name = "duration", nullable = false)
    var duration: String = "00:00:00",

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "game_type(0, 0) not null", nullable = false)
    var type: GameType,

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", columnDefinition = "game_difficulty(0, 0) not null", nullable = false)
    var difficulty: GameDifficulty,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "game_status(0, 0) not null", nullable = false)
    var status: GameStatus = GameStatus.IN_PROGRESS,

    @Column(name = "language", columnDefinition = "language_name(0, 0) not null")
    @Enumerated(EnumType.STRING)
    var language: LanguageName,

    @Column(name = "instruction", nullable = false, columnDefinition = "json")
    var instruction: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    override var user: User,

    @Column(name = "user_id", insertable = false, updatable = false)
    var userId: UUID = user.id,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
) : IdentifiableUserResource {
    @PostLoad
    fun populateUserId() {
        userId = user.id
    }
}