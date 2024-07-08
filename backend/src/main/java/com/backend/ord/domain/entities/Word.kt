package com.backend.ord.domain.entities;

import com.backend.ord.domain.embedded.ExampleSentence;
import com.backend.ord.domain.entities.abstracts.EntityBase;
import com.backend.ord.enums.Language.LanguageName;
import com.backend.ord.enums.Word.WordType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "words")
public class Word extends EntityBase {
    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "translation", nullable = false)
    private String translation;

    @Column(name = "is_bookmarked", nullable = false)
    private Boolean isBookmarked = false;

    @Column(name = "points", nullable = false)
    private Integer points = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private WordType type;

    @Column(name = "translated_from", nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageName translatedFrom;

    @Column(name = "translated_to", columnDefinition = "language_name(0, 0) not null")
    @Enumerated(EnumType.STRING)
    private LanguageName translatedTo;

    @Column(name = "example_sentences", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<ExampleSentence> exampleSentences = new HashSet<ExampleSentence>();

    public String getOrigin() {
        return this.origin;
    }

    public String getTranslation() {
        return this.translation;
    }

    public Boolean getIsBookmarked() {
        return this.isBookmarked;
    }

    public Integer getPoints() {
        return this.points;
    }

    public Bank getBank() {
        return this.bank;
    }

    public User getUser() {
        return this.user;
    }

    public WordType getType() {
        return this.type;
    }

    public LanguageName getTranslatedFrom() {
        return this.translatedFrom;
    }

    public LanguageName getTranslatedTo() {
        return this.translatedTo;
    }

    public Set<ExampleSentence> getExampleSentences() {
        return this.exampleSentences;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setIsBookmarked(Boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setType(WordType type) {
        this.type = type;
    }

    public void setTranslatedFrom(LanguageName translatedFrom) {
        this.translatedFrom = translatedFrom;
    }

    public void setTranslatedTo(LanguageName translatedTo) {
        this.translatedTo = translatedTo;
    }

    public void setExampleSentences(Set<ExampleSentence> exampleSentences) {
        this.exampleSentences = exampleSentences;
    }
}