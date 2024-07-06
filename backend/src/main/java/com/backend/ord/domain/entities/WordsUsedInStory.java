package com.backend.ord.domain.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "words_used_in_stories")
public class WordsUsedInStory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "story_id")
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "word_id")
    private Word word;

    public UUID getId() {
        return this.id;
    }

    public Story getStory() {
        return this.story;
    }

    public Word getWord() {
        return this.word;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public void setWord(Word word) {
        this.word = word;
    }
}