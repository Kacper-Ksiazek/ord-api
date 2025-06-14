package com.backend.ord.features.game.model.ongoing_game

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.enums.GameType
import com.backend.ord.shared.models.IdentifiableUserResource
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.Instant
import java.util.*

@Entity
@Table(name = "ongoing_games")
data class OngoingGameEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @Column(name = "proper_answers", columnDefinition = "json")
    var properAnswers: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "game_type(0, 0) not null", nullable = false)
    var type: GameType,

    @Column(name = "language", columnDefinition = "language_name(0, 0) not null")
    @Enumerated(EnumType.STRING)
    var language: LanguageName,

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", columnDefinition = "game_difficulty(0, 0) not null", nullable = false)
    var difficulty: GameDifficulty,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    override var user: UserEntity,

    @Column(name = "user_id", insertable = false, updatable = false)
    var userId: UUID = user.id,

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now()
) : IdentifiableUserResource {
    @PostLoad
    fun populateUserId() {
        userId = user.id
    }
}