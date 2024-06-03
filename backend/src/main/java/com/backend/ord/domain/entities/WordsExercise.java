package com.backend.ord.domain.entities;

import com.backend.ord.enums.WordsExercise.WordsExerciseStatus;
import com.backend.ord.enums.WordsExercise.WordsExerciseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "words_exercises")
public class WordsExercise extends EntityBase {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "final_score", nullable = false)
    private Integer finalScore;

    @NotNull
    @Column(name = "involved_words", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<UUID> involvedWords;

    @NotNull
    @Column(name = "acquired_points", nullable = false)
    private Integer acquiredPoints;

    @NotNull
    @Column(name = "involved_banks", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<UUID> involvedBanks;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "words_exercises_type(0, 0) not null", nullable = false)
    private WordsExerciseType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "words_exercises_status(0, 0) not null", nullable = false)
    private WordsExerciseStatus status;
}