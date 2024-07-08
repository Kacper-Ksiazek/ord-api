package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "stories")
public class Story extends EntityBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 64)
    @NotNull
    @Column(name = "title", nullable = false, length = 64)
    private String title;

    @NotNull
    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    // In the following `Map` structure keys are the words and values are the explanations of their meanings, followed by a brief explaining how they contribute to the story.
    @NotNull
    @Column(name = "explanations", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> explanations;

    @NotNull
    @Column(name = "number_of_tokens", nullable = false)
    private Integer numberOfTokens;

    public User getUser() {
        return this.user;
    }

    public @Size(max = 64) @NotNull String getTitle() {
        return this.title;
    }

    public @NotNull String getContent() {
        return this.content;
    }

    public @NotNull Map<String, String> getExplanations() {
        return this.explanations;
    }

    public @NotNull Integer getNumberOfTokens() {
        return this.numberOfTokens;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTitle(@Size(max = 64) @NotNull String title) {
        this.title = title;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }

    public void setExplanations(@NotNull Map<String, String> explanations) {
        this.explanations = explanations;
    }

    public void setNumberOfTokens(@NotNull Integer numberOfTokens) {
        this.numberOfTokens = numberOfTokens;
    }
}