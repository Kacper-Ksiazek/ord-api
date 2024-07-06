package com.backend.ord.domain.entities.gpt_tokens_usage;

import com.backend.ord.domain.entities.Game;
import com.backend.ord.domain.entities.User;
import com.backend.ord.domain.entities.abstracts.EntityBase;
import com.backend.ord.enums.TokensUsage.GamesGPTTokensConsumptionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    public User getUser() {
        return this.user;
    }

    public Game getGames() {
        return this.games;
    }

    public @NotNull @Min(1) Integer getNumberOfGenerations() {
        return this.numberOfGenerations;
    }

    public @NotNull @Min(0) Integer getNumberOfTokens() {
        return this.numberOfTokens;
    }

    public GamesGPTTokensConsumptionType getConsumptionType() {
        return this.consumptionType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGames(Game games) {
        this.games = games;
    }

    public void setNumberOfGenerations(@NotNull @Min(1) Integer numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public void setNumberOfTokens(@NotNull @Min(0) Integer numberOfTokens) {
        this.numberOfTokens = numberOfTokens;
    }

    public void setConsumptionType(GamesGPTTokensConsumptionType consumptionType) {
        this.consumptionType = consumptionType;
    }
}