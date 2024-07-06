package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;
import com.backend.ord.domain.embedded.ExampleSentence;
import com.backend.ord.enums.Language.LanguageName;
import com.backend.ord.enums.Word.WordType;

import java.util.HashSet;
import java.util.Set;

public class WordsDTO extends DTOBase {
    private String origin;
    private String translation;
    private Boolean isBookmarked;
    private WordType type;
    private LanguageName translatedFrom;
    private LanguageName translatedTo;

    private Integer points = 0;
    private Set<ExampleSentence> exampleSentences = new HashSet<ExampleSentence>();

    private BankDTO bank;
    private UserDTO user;

    public WordsDTO(String origin, String translation, Boolean isBookmarked, WordType type, LanguageName translatedFrom, LanguageName translatedTo, Integer points, Set<ExampleSentence> exampleSentences, BankDTO bank, UserDTO user) {
        this.origin = origin;
        this.translation = translation;
        this.isBookmarked = isBookmarked;
        this.type = type;
        this.translatedFrom = translatedFrom;
        this.translatedTo = translatedTo;
        this.points = points;
        this.exampleSentences = exampleSentences;
        this.bank = bank;
        this.user = user;
    }

    public WordsDTO() {
    }

    public static WordsDTOBuilder builder() {
        return new WordsDTOBuilder();
    }

    public String getOrigin() {
        return this.origin;
    }

    public String getTranslation() {
        return this.translation;
    }

    public Boolean getIsBookmarked() {
        return this.isBookmarked;
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

    public Integer getPoints() {
        return this.points;
    }

    public Set<ExampleSentence> getExampleSentences() {
        return this.exampleSentences;
    }

    public BankDTO getBank() {
        return this.bank;
    }

    public UserDTO getUser() {
        return this.user;
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

    public void setType(WordType type) {
        this.type = type;
    }

    public void setTranslatedFrom(LanguageName translatedFrom) {
        this.translatedFrom = translatedFrom;
    }

    public void setTranslatedTo(LanguageName translatedTo) {
        this.translatedTo = translatedTo;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setExampleSentences(Set<ExampleSentence> exampleSentences) {
        this.exampleSentences = exampleSentences;
    }

    public void setBank(BankDTO bank) {
        this.bank = bank;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String toString() {
        return "WordsDTO(origin=" + this.getOrigin() + ", translation=" + this.getTranslation() + ", isBookmarked=" + this.getIsBookmarked() + ", type=" + this.getType() + ", translatedFrom=" + this.getTranslatedFrom() + ", translatedTo=" + this.getTranslatedTo() + ", points=" + this.getPoints() + ", exampleSentences=" + this.getExampleSentences() + ", bank=" + this.getBank() + ", user=" + this.getUser() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof WordsDTO)) return false;
        final WordsDTO other = (WordsDTO) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$origin = this.getOrigin();
        final Object other$origin = other.getOrigin();
        if (this$origin == null ? other$origin != null : !this$origin.equals(other$origin)) return false;
        final Object this$translation = this.getTranslation();
        final Object other$translation = other.getTranslation();
        if (this$translation == null ? other$translation != null : !this$translation.equals(other$translation))
            return false;
        final Object this$isBookmarked = this.getIsBookmarked();
        final Object other$isBookmarked = other.getIsBookmarked();
        if (this$isBookmarked == null ? other$isBookmarked != null : !this$isBookmarked.equals(other$isBookmarked))
            return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$translatedFrom = this.getTranslatedFrom();
        final Object other$translatedFrom = other.getTranslatedFrom();
        if (this$translatedFrom == null ? other$translatedFrom != null : !this$translatedFrom.equals(other$translatedFrom))
            return false;
        final Object this$translatedTo = this.getTranslatedTo();
        final Object other$translatedTo = other.getTranslatedTo();
        if (this$translatedTo == null ? other$translatedTo != null : !this$translatedTo.equals(other$translatedTo))
            return false;
        final Object this$points = this.getPoints();
        final Object other$points = other.getPoints();
        if (this$points == null ? other$points != null : !this$points.equals(other$points)) return false;
        final Object this$exampleSentences = this.getExampleSentences();
        final Object other$exampleSentences = other.getExampleSentences();
        if (this$exampleSentences == null ? other$exampleSentences != null : !this$exampleSentences.equals(other$exampleSentences))
            return false;
        final Object this$bank = this.getBank();
        final Object other$bank = other.getBank();
        if (this$bank == null ? other$bank != null : !this$bank.equals(other$bank)) return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof WordsDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $origin = this.getOrigin();
        result = result * PRIME + ($origin == null ? 43 : $origin.hashCode());
        final Object $translation = this.getTranslation();
        result = result * PRIME + ($translation == null ? 43 : $translation.hashCode());
        final Object $isBookmarked = this.getIsBookmarked();
        result = result * PRIME + ($isBookmarked == null ? 43 : $isBookmarked.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $translatedFrom = this.getTranslatedFrom();
        result = result * PRIME + ($translatedFrom == null ? 43 : $translatedFrom.hashCode());
        final Object $translatedTo = this.getTranslatedTo();
        result = result * PRIME + ($translatedTo == null ? 43 : $translatedTo.hashCode());
        final Object $points = this.getPoints();
        result = result * PRIME + ($points == null ? 43 : $points.hashCode());
        final Object $exampleSentences = this.getExampleSentences();
        result = result * PRIME + ($exampleSentences == null ? 43 : $exampleSentences.hashCode());
        final Object $bank = this.getBank();
        result = result * PRIME + ($bank == null ? 43 : $bank.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        return result;
    }

    public static class WordsDTOBuilder {
        private String origin;
        private String translation;
        private Boolean isBookmarked;
        private WordType type;
        private LanguageName translatedFrom;
        private LanguageName translatedTo;
        private Integer points;
        private Set<ExampleSentence> exampleSentences;
        private BankDTO bank;
        private UserDTO user;

        WordsDTOBuilder() {
        }

        public WordsDTOBuilder origin(String origin) {
            this.origin = origin;
            return this;
        }

        public WordsDTOBuilder translation(String translation) {
            this.translation = translation;
            return this;
        }

        public WordsDTOBuilder isBookmarked(Boolean isBookmarked) {
            this.isBookmarked = isBookmarked;
            return this;
        }

        public WordsDTOBuilder type(WordType type) {
            this.type = type;
            return this;
        }

        public WordsDTOBuilder translatedFrom(LanguageName translatedFrom) {
            this.translatedFrom = translatedFrom;
            return this;
        }

        public WordsDTOBuilder translatedTo(LanguageName translatedTo) {
            this.translatedTo = translatedTo;
            return this;
        }

        public WordsDTOBuilder points(Integer points) {
            this.points = points;
            return this;
        }

        public WordsDTOBuilder exampleSentences(Set<ExampleSentence> exampleSentences) {
            this.exampleSentences = exampleSentences;
            return this;
        }

        public WordsDTOBuilder bank(BankDTO bank) {
            this.bank = bank;
            return this;
        }

        public WordsDTOBuilder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public WordsDTO build() {
            return new WordsDTO(this.origin, this.translation, this.isBookmarked, this.type, this.translatedFrom, this.translatedTo, this.points, this.exampleSentences, this.bank, this.user);
        }

        public String toString() {
            return "WordsDTO.WordsDTOBuilder(origin=" + this.origin + ", translation=" + this.translation + ", isBookmarked=" + this.isBookmarked + ", type=" + this.type + ", translatedFrom=" + this.translatedFrom + ", translatedTo=" + this.translatedTo + ", points=" + this.points + ", exampleSentences=" + this.exampleSentences + ", bank=" + this.bank + ", user=" + this.user + ")";
        }
    }
}

