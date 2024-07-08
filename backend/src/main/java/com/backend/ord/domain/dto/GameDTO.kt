package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;

public class GameDTO extends DTOBase {
    private Integer finalScore;

    private UserDTO user;

    public GameDTO(Integer finalScore, UserDTO user) {
        this.finalScore = finalScore;
        this.user = user;
    }

    public GameDTO() {
    }

    public static GameDTOBuilder builder() {
        return new GameDTOBuilder();
    }

    public Integer getFinalScore() {
        return this.finalScore;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String toString() {
        return "GameDTO(finalScore=" + this.getFinalScore() + ", user=" + this.getUser() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof GameDTO)) return false;
        final GameDTO other = (GameDTO) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$finalScore = this.getFinalScore();
        final Object other$finalScore = other.getFinalScore();
        if (this$finalScore == null ? other$finalScore != null : !this$finalScore.equals(other$finalScore))
            return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof GameDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $finalScore = this.getFinalScore();
        result = result * PRIME + ($finalScore == null ? 43 : $finalScore.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        return result;
    }

    public static class GameDTOBuilder {
        private Integer finalScore;
        private UserDTO user;

        GameDTOBuilder() {
        }

        public GameDTOBuilder finalScore(Integer finalScore) {
            this.finalScore = finalScore;
            return this;
        }

        public GameDTOBuilder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public GameDTO build() {
            return new GameDTO(this.finalScore, this.user);
        }

        public String toString() {
            return "GameDTO.GameDTOBuilder(finalScore=" + this.finalScore + ", user=" + this.user + ")";
        }
    }
}
