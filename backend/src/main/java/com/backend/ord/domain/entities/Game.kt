package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import com.backend.ord.enums.Game.GameStatus;
import com.backend.ord.enums.Game.GameType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "games")
public class Game extends EntityBase {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "final_score", nullable = false)
    private Integer finalScore;

    @NotNull
    @Column(name = "acquired_points", nullable = false)
    private Integer acquiredPoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "game_type(0, 0) not null", nullable = false)
    private GameType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "game_status(0, 0) not null", nullable = false)
    private GameStatus status;

    public @NotNull User getUser() {
        return this.user;
    }

    public @NotNull Integer getFinalScore() {
        return this.finalScore;
    }

    public @NotNull Integer getAcquiredPoints() {
        return this.acquiredPoints;
    }

    public GameType getType() {
        return this.type;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    public void setFinalScore(@NotNull Integer finalScore) {
        this.finalScore = finalScore;
    }

    public void setAcquiredPoints(@NotNull Integer acquiredPoints) {
        this.acquiredPoints = acquiredPoints;
    }

    public void setType(GameType type) {
        this.type = type;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}