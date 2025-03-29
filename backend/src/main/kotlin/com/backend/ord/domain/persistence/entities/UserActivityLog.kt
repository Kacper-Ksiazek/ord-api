package com.backend.ord.domain.persistence.entities

import com.backend.ord.domain.persistence.entities.interfaces.IdentifiableUserResource
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "user_activity_logs")
data class UserActivityLog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID = UUID.randomUUID(),

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    var type: UserActivityType,

    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    var language: LanguageName,

    @Enumerated(EnumType.STRING)
    @Column(name = "game_difficulty", nullable = true)
    var gameDifficulty: GameDifficulty? = null,

    @Column(name = "points", nullable = false)
    @field:Min(0)
    var points: Int = type.points,

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