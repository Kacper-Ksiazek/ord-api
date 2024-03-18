package com.backend.ord.domain.entities;

import com.backend.ord.enums.LanguageName;
import com.backend.ord.enums.LanguageProficiencyLevel;
import com.backend.ord.enums.converters.LanguageNameConverter;
import com.backend.ord.enums.converters.LanguageProficiencyLevelConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "language_proficiencies")
public class LanguageProficiency extends EntityBase {
    @Column(name = "language", nullable = false)
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
