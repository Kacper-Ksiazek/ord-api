package com.backend.ord.domain.entities.gpt_tokens_usage;

import com.backend.ord.domain.entities.Game;
import com.backend.ord.domain.entities.User;
import com.backend.ord.domain.entities.abstracts.EntityBase;
import com.backend.ord.enums.TokensUsage.GamesGPTTokensConsumptionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "game_tokens_usages")
public class GameTokensUsage extends EntityBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "game_id")
    private Game games;

    @NotNull
    @Column(name = "number_of_generations", nullable = false)
    @Min(1)
    private Integer numberOfGenerations = 1;

    @NotNull
    @Column(name = "number_of_tokens", nullable = false)
    @Min(0)
    private Integer numberOfTokens = 0;

    @Column(name = "consumption_type", columnDefinition = "games_gpt_tokens_consumption_type(0, 0) not null")
    @Enumerated(EnumType.STRING)
    private GamesGPTTokensConsumptionType consumptionType;
}