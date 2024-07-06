package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import com.backend.ord.enums.Language.LanguageName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "quickly_added_words")
public class QuicklyAddedWord extends EntityBase {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 255)
    @NotNull
    @Column(name = "typed_word", nullable = false)
    private String typedWord;

    @Column(name = "typed_in_language", columnDefinition = "language_name(0, 0) not null")
    @Enumerated(EnumType.STRING)
    private LanguageName typedInLanguage;

    public @NotNull User getUser() {
        return this.user;
    }

    public @Size(max = 255) @NotNull String getTypedWord() {
        return this.typedWord;
    }

    public LanguageName getTypedInLanguage() {
        return this.typedInLanguage;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    public void setTypedWord(@Size(max = 255) @NotNull String typedWord) {
        this.typedWord = typedWord;
    }

    public void setTypedInLanguage(LanguageName typedInLanguage) {
        this.typedInLanguage = typedInLanguage;
    }
}