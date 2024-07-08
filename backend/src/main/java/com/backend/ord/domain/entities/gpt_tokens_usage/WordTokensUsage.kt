package com.backend.ord.domain.entities.gpt_tokens_usage;

import com.backend.ord.domain.entities.User;
import com.backend.ord.domain.entities.Word;
import com.backend.ord.domain.entities.abstracts.EntityBase;
import com.backend.ord.enums.TokensUsage.WordsGPTTokensConsumptionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "word_tokens_usages")
public class WordTokensUsage extends EntityBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "word_id")
    private Word words;

    @NotNull
    @Column(name = "number_of_generations", nullable = false)
    @Min(1)
    private Integer numberOfGenerations = 1;

    @NotNull
    @Column(name = "number_of_tokens", nullable = false)
    @Min(0)
    private Integer numberOfTokens = 0;

    @Column(name = "consumption_type", columnDefinition = "words_gpt_tokens_consumption_type(0, 0) not null")
    @Enumerated(EnumType.STRING)
    private WordsGPTTokensConsumptionType consumptionType;

    public User getUser() {
        return this.user;
    }

    public Word getWords() {
        return this.words;
    }

    public @NotNull @Min(1) Integer getNumberOfGenerations() {
        return this.numberOfGenerations;
    }

    public @NotNull @Min(0) Integer getNumberOfTokens() {
        return this.numberOfTokens;
    }

    public WordsGPTTokensConsumptionType getConsumptionType() {
        return this.consumptionType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setWords(Word words) {
        this.words = words;
    }

    public void setNumberOfGenerations(@NotNull @Min(1) Integer numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public void setNumberOfTokens(@NotNull @Min(0) Integer numberOfTokens) {
        this.numberOfTokens = numberOfTokens;
    }

    public void setConsumptionType(WordsGPTTokensConsumptionType consumptionType) {
        this.consumptionType = consumptionType;
    }
}