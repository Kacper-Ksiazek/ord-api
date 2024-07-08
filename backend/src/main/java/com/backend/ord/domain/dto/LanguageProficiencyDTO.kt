package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import com.backend.ord.enums.Language.LanguageName;
import com.backend.ord.enums.Language.LanguageProficiencyLevel;

public class LanguageProficiencyDTO extends DTOBase {
    private LanguageName language;
    private LanguageProficiencyLevel proficiency;

    private UserDTO user;

    public LanguageProficiencyDTO(LanguageName language, LanguageProficiencyLevel proficiency, UserDTO user) {
        this.language = language;
        this.proficiency = proficiency;
        this.user = user;
    }

    public LanguageProficiencyDTO() {
    }

    public static LanguageProficiencyDTOBuilder builder() {
        return new LanguageProficiencyDTOBuilder();
    }

    public LanguageName getLanguage() {
        return this.language;
    }

    public LanguageProficiencyLevel getProficiency() {
        return this.proficiency;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public void setLanguage(LanguageName language) {
        this.language = language;
    }

    public void setProficiency(LanguageProficiencyLevel proficiency) {
        this.proficiency = proficiency;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String toString() {
        return "LanguageProficiencyDTO(language=" + this.getLanguage() + ", proficiency=" + this.getProficiency() + ", user=" + this.getUser() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof LanguageProficiencyDTO)) return false;
        final LanguageProficiencyDTO other = (LanguageProficiencyDTO) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$language = this.getLanguage();
        final Object other$language = other.getLanguage();
        if (this$language == null ? other$language != null : !this$language.equals(other$language)) return false;
        final Object this$proficiency = this.getProficiency();
        final Object other$proficiency = other.getProficiency();
        if (this$proficiency == null ? other$proficiency != null : !this$proficiency.equals(other$proficiency))
            return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof LanguageProficiencyDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $language = this.getLanguage();
        result = result * PRIME + ($language == null ? 43 : $language.hashCode());
        final Object $proficiency = this.getProficiency();
        result = result * PRIME + ($proficiency == null ? 43 : $proficiency.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        return result;
    }

    public static class LanguageProficiencyDTOBuilder {
        private LanguageName language;
        private LanguageProficiencyLevel proficiency;
        private UserDTO user;

        LanguageProficiencyDTOBuilder() {
        }

        public LanguageProficiencyDTOBuilder language(LanguageName language) {
            this.language = language;
            return this;
        }

        public LanguageProficiencyDTOBuilder proficiency(LanguageProficiencyLevel proficiency) {
            this.proficiency = proficiency;
            return this;
        }

        public LanguageProficiencyDTOBuilder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public LanguageProficiencyDTO build() {
            return new LanguageProficiencyDTO(this.language, this.proficiency, this.user);
        }

        public String toString() {
            return "LanguageProficiencyDTO.LanguageProficiencyDTOBuilder(language=" + this.language + ", proficiency=" + this.proficiency + ", user=" + this.user + ")";
        }
    }
}
