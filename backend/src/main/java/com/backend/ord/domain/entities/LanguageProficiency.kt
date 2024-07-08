package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import com.backend.ord.enums.Language.LanguageName;
import com.backend.ord.enums.Language.LanguageProficiencyLevel;
import jakarta.persistence.*;

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

    public LanguageProficiency(LanguageName language, LanguageProficiencyLevel proficiency, User user) {
        this.language = language;
        this.proficiency = proficiency;
        this.user = user;
    }

    public LanguageProficiency() {
    }

    public static LanguageProficiencyBuilder builder() {
        return new LanguageProficiencyBuilder();
    }

    public LanguageName getLanguage() {
        return this.language;
    }

    public LanguageProficiencyLevel getProficiency() {
        return this.proficiency;
    }

    public User getUser() {
        return this.user;
    }

    public void setLanguage(LanguageName language) {
        this.language = language;
    }

    public void setProficiency(LanguageProficiencyLevel proficiency) {
        this.proficiency = proficiency;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class LanguageProficiencyBuilder {
        private LanguageName language;
        private LanguageProficiencyLevel proficiency;
        private User user;

        LanguageProficiencyBuilder() {
        }

        public LanguageProficiencyBuilder language(LanguageName language) {
            this.language = language;
            return this;
        }

        public LanguageProficiencyBuilder proficiency(LanguageProficiencyLevel proficiency) {
            this.proficiency = proficiency;
            return this;
        }

        public LanguageProficiencyBuilder user(User user) {
            this.user = user;
            return this;
        }

        public LanguageProficiency build() {
            return new LanguageProficiency(this.language, this.proficiency, this.user);
        }

        public String toString() {
            return "LanguageProficiency.LanguageProficiencyBuilder(language=" + this.language + ", proficiency=" + this.proficiency + ", user=" + this.user + ")";
        }
    }
}
