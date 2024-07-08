package com.backend.ord.domain.entities.gpt_tokens_usage;

import com.backend.ord.domain.entities.Story;
import com.backend.ord.domain.entities.User;
import com.backend.ord.domain.entities.abstracts.EntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "story_tokens_usages")
public class StoryTokensUsage extends EntityBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "story_id")
    private Story stories;

    @NotNull
    @Column(name = "number_of_generations", nullable = false)
    private Integer numberOfGenerations;

    @NotNull
    @Column(name = "number_of_tokens", nullable = false)
    private Integer numberOfTokens;

    public User getUser() {
        return this.user;
    }

    public Story getStories() {
        return this.stories;
    }

    public @NotNull Integer getNumberOfGenerations() {
        return this.numberOfGenerations;
    }

    public @NotNull Integer getNumberOfTokens() {
        return this.numberOfTokens;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStories(Story stories) {
        this.stories = stories;
    }

    public void setNumberOfGenerations(@NotNull Integer numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public void setNumberOfTokens(@NotNull Integer numberOfTokens) {
        this.numberOfTokens = numberOfTokens;
    }
}