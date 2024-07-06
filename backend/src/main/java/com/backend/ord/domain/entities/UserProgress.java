package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "user_progress")
public class UserProgress extends EntityBase {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @NotNull
    @Column(name = "points_obtained", nullable = false)
    private Integer pointsObtained;

    public @NotNull User getUser() {
        return this.user;
    }

    public @NotNull Game getGame() {
        return this.game;
    }

    public @NotNull Integer getPointsObtained() {
        return this.pointsObtained;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    public void setGame(@NotNull Game game) {
        this.game = game;
    }

    public void setPointsObtained(@NotNull Integer pointsObtained) {
        this.pointsObtained = pointsObtained;
    }
}