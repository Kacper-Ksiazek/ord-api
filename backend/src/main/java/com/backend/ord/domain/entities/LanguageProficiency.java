package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import com.backend.ord.enums.Language.LanguageName;
import com.backend.ord.enums.Language.LanguageProficiencyLevel;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "language_proficiencies")
public class LanguageProficiency extends EntityBase {
    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageName language;

    @Column(name = "proficiency", nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageProficiencyLevel proficiency;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
