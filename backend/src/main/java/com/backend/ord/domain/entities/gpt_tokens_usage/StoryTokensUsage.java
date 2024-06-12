package com.backend.ord.domain.entities.gpt_tokens_usage;

import com.backend.ord.domain.entities.Story;
import com.backend.ord.domain.entities.User;
import com.backend.ord.domain.entities.abstracts.EntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
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

}