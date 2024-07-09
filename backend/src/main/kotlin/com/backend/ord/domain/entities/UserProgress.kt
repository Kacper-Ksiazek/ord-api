package com.backend.ord.domain.entities

import com.backend.ord.domain.entities.abstracts.EntityBase
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "user_progress")
class UserProgress(
    @Column(name = "points_obtained", nullable = false)
    @field:Size(min = 0)
    var pointsObtained: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "game_id", nullable = false)
    var game: Game
) : EntityBase()