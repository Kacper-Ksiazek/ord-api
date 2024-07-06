package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import com.backend.ord.enums.Language.LanguageName;

public class QuicklyAddedWordDTO extends DTOBase {
    private String typedWord;
    private LanguageName typedInLanguage;

    private UserDTO user;

    public QuicklyAddedWordDTO(String typedWord, LanguageName typedInLanguage, UserDTO user) {
        this.typedWord = typedWord;
        this.typedInLanguage = typedInLanguage;
        this.user = user;
    }

    public QuicklyAddedWordDTO() {
    }

    public static QuicklyAddedWordDTOBuilder builder() {
        return new QuicklyAddedWordDTOBuilder();
    }

    public String getTypedWord() {
        return this.typedWord;
    }

    public LanguageName getTypedInLanguage() {
        return this.typedInLanguage;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public void setTypedWord(String typedWord) {
        this.typedWord = typedWord;
    }

    public void setTypedInLanguage(LanguageName typedInLanguage) {
        this.typedInLanguage = typedInLanguage;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String toString() {
        return "QuicklyAddedWordDTO(typedWord=" + this.getTypedWord() + ", typedInLanguage=" + this.getTypedInLanguage() + ", user=" + this.getUser() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof QuicklyAddedWordDTO)) return false;
        final QuicklyAddedWordDTO other = (QuicklyAddedWordDTO) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$typedWord = this.getTypedWord();
        final Object other$typedWord = other.getTypedWord();
        if (this$typedWord == null ? other$typedWord != null : !this$typedWord.equals(other$typedWord)) return false;
        final Object this$typedInLanguage = this.getTypedInLanguage();
        final Object other$typedInLanguage = other.getTypedInLanguage();
        if (this$typedInLanguage == null ? other$typedInLanguage != null : !this$typedInLanguage.equals(other$typedInLanguage))
            return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof QuicklyAddedWordDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $typedWord = this.getTypedWord();
        result = result * PRIME + ($typedWord == null ? 43 : $typedWord.hashCode());
        final Object $typedInLanguage = this.getTypedInLanguage();
        result = result * PRIME + ($typedInLanguage == null ? 43 : $typedInLanguage.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        return result;
    }

    public static class QuicklyAddedWordDTOBuilder {
        private String typedWord;
        private LanguageName typedInLanguage;
        private UserDTO user;

        QuicklyAddedWordDTOBuilder() {
        }

        public QuicklyAddedWordDTOBuilder typedWord(String typedWord) {
            this.typedWord = typedWord;
            return this;
        }

        public QuicklyAddedWordDTOBuilder typedInLanguage(LanguageName typedInLanguage) {
            this.typedInLanguage = typedInLanguage;
            return this;
        }

        public QuicklyAddedWordDTOBuilder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public QuicklyAddedWordDTO build() {
            return new QuicklyAddedWordDTO(this.typedWord, this.typedInLanguage, this.user);
        }

        public String toString() {
            return "QuicklyAddedWordDTO.QuicklyAddedWordDTOBuilder(typedWord=" + this.typedWord + ", typedInLanguage=" + this.typedInLanguage + ", user=" + this.user + ")";
        }
    }
}
