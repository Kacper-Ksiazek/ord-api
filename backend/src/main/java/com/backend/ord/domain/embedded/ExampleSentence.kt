package com.backend.ord.domain.embedded;

public class ExampleSentence {
    private String sentence;
    private String translation;

    ExampleSentence(String sentence, String translation) {
        this.sentence = sentence;
        this.translation = translation;
    }

    public static ExampleSentenceBuilder builder() {
        return new ExampleSentenceBuilder();
    }

    public String getSentence() {
        return this.sentence;
    }

    public String getTranslation() {
        return this.translation;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ExampleSentence)) return false;
        final ExampleSentence other = (ExampleSentence) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$sentence = this.getSentence();
        final Object other$sentence = other.getSentence();
        if (this$sentence == null ? other$sentence != null : !this$sentence.equals(other$sentence)) return false;
        final Object this$translation = this.getTranslation();
        final Object other$translation = other.getTranslation();
        if (this$translation == null ? other$translation != null : !this$translation.equals(other$translation))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ExampleSentence;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $sentence = this.getSentence();
        result = result * PRIME + ($sentence == null ? 43 : $sentence.hashCode());
        final Object $translation = this.getTranslation();
        result = result * PRIME + ($translation == null ? 43 : $translation.hashCode());
        return result;
    }

    public String toString() {
        return "ExampleSentence(sentence=" + this.getSentence() + ", translation=" + this.getTranslation() + ")";
    }

    public static class ExampleSentenceBuilder {
        private String sentence;
        private String translation;

        ExampleSentenceBuilder() {
        }

        public ExampleSentenceBuilder sentence(String sentence) {
            this.sentence = sentence;
            return this;
        }

        public ExampleSentenceBuilder translation(String translation) {
            this.translation = translation;
            return this;
        }

        public ExampleSentence build() {
            return new ExampleSentence(this.sentence, this.translation);
        }

        public String toString() {
            return "ExampleSentence.ExampleSentenceBuilder(sentence=" + this.sentence + ", translation=" + this.translation + ")";
        }
    }
}
