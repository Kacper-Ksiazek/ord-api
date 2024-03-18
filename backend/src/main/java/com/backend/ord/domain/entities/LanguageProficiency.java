package com.backend.ord.domain.entities;

import com.backend.ord.enums.LanguageName;
import com.backend.ord.enums.LanguageProficiencyLevel;
import com.backend.ord.enums.converters.LanguageNameConverter;
import com.backend.ord.enums.converters.LanguageProficiencyLevelConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "language_proficiencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageProficiency {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "language", nullable = false)
    @Convert(converter = LanguageProficiencyLevel.class)
    @Convert(converter = LanguageNameConverter.class)
    private LanguageName language;

    @Column(nullable = false)
    @Convert(converter = LanguageProficiencyLevelConverter.class)
    private LanguageProficiencyLevel proficiency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            insertable = false
    )
    private User user;
}
