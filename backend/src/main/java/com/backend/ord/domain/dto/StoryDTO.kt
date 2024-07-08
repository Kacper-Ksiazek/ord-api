package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;

import java.util.Map;

public class StoryDTO extends DTOBase {
    private String title;
    private String content;
    private Integer numberOfTokens;
    private Map<String, String> explanations;

    private UserDTO user;

    public StoryDTO(String title, String content, Integer numberOfTokens, Map<String, String> explanations, UserDTO user) {
        this.title = title;
        this.content = content;
        this.numberOfTokens = numberOfTokens;
        this.explanations = explanations;
        this.user = user;
    }

    public StoryDTO() {
    }

    public static StoryDTOBuilder builder() {
        return new StoryDTOBuilder();
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public Integer getNumberOfTokens() {
        return this.numberOfTokens;
    }

    public Map<String, String> getExplanations() {
        return this.explanations;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNumberOfTokens(Integer numberOfTokens) {
        this.numberOfTokens = numberOfTokens;
    }

    public void setExplanations(Map<String, String> explanations) {
        this.explanations = explanations;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String toString() {
        return "StoryDTO(title=" + this.getTitle() + ", content=" + this.getContent() + ", numberOfTokens=" + this.getNumberOfTokens() + ", explanations=" + this.getExplanations() + ", user=" + this.getUser() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof StoryDTO)) return false;
        final StoryDTO other = (StoryDTO) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$title = this.getTitle();
        final Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        final Object this$content = this.getContent();
        final Object other$content = other.getContent();
        if (this$content == null ? other$content != null : !this$content.equals(other$content)) return false;
        final Object this$numberOfTokens = this.getNumberOfTokens();
        final Object other$numberOfTokens = other.getNumberOfTokens();
        if (this$numberOfTokens == null ? other$numberOfTokens != null : !this$numberOfTokens.equals(other$numberOfTokens))
            return false;
        final Object this$explanations = this.getExplanations();
        final Object other$explanations = other.getExplanations();
        if (this$explanations == null ? other$explanations != null : !this$explanations.equals(other$explanations))
            return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof StoryDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        final Object $content = this.getContent();
        result = result * PRIME + ($content == null ? 43 : $content.hashCode());
        final Object $numberOfTokens = this.getNumberOfTokens();
        result = result * PRIME + ($numberOfTokens == null ? 43 : $numberOfTokens.hashCode());
        final Object $explanations = this.getExplanations();
        result = result * PRIME + ($explanations == null ? 43 : $explanations.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        return result;
    }

    public static class StoryDTOBuilder {
        private String title;
        private String content;
        private Integer numberOfTokens;
        private Map<String, String> explanations;
        private UserDTO user;

        StoryDTOBuilder() {
        }

        public StoryDTOBuilder title(String title) {
            this.title = title;
            return this;
        }

        public StoryDTOBuilder content(String content) {
            this.content = content;
            return this;
        }

        public StoryDTOBuilder numberOfTokens(Integer numberOfTokens) {
            this.numberOfTokens = numberOfTokens;
            return this;
        }

        public StoryDTOBuilder explanations(Map<String, String> explanations) {
            this.explanations = explanations;
            return this;
        }

        public StoryDTOBuilder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public StoryDTO build() {
            return new StoryDTO(this.title, this.content, this.numberOfTokens, this.explanations, this.user);
        }

        public String toString() {
            return "StoryDTO.StoryDTOBuilder(title=" + this.title + ", content=" + this.content + ", numberOfTokens=" + this.numberOfTokens + ", explanations=" + this.explanations + ", user=" + this.user + ")";
        }
    }
}
