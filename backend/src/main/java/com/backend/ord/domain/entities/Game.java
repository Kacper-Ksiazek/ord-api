package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import com.backend.ord.enums.Game.GameStatus;
import com.backend.ord.enums.Game.GameType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
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
}