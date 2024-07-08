package com.backend.ord.domain.dto;

import com.backend.ord.domain.dto.abstracts.DTOBase;

public class UserProgressDTO extends DTOBase {
    private Integer pointsObtained;

    private UserDTO user;
    private GameDTO game;

    public UserProgressDTO(Integer pointsObtained, UserDTO user, GameDTO game) {
        this.pointsObtained = pointsObtained;
        this.user = user;
        this.game = game;
    }

    public UserProgressDTO() {
    }

    public static UserProgressDTOBuilder builder() {
        return new UserProgressDTOBuilder();
    }

    public Integer getPointsObtained() {
        return this.pointsObtained;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public GameDTO getGame() {
        return this.game;
    }

    public void setPointsObtained(Integer pointsObtained) {
        this.pointsObtained = pointsObtained;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setGame(GameDTO game) {
        this.game = game;
    }

    public String toString() {
        return "UserProgressDTO(pointsObtained=" + this.getPointsObtained() + ", user=" + this.getUser() + ", game=" + this.getGame() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof UserProgressDTO)) return false;
        final UserProgressDTO other = (UserProgressDTO) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$pointsObtained = this.getPointsObtained();
        final Object other$pointsObtained = other.getPointsObtained();
        if (this$pointsObtained == null ? other$pointsObtained != null : !this$pointsObtained.equals(other$pointsObtained))
            return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        final Object this$game = this.getGame();
        final Object other$game = other.getGame();
        if (this$game == null ? other$game != null : !this$game.equals(other$game)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserProgressDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $pointsObtained = this.getPointsObtained();
        result = result * PRIME + ($pointsObtained == null ? 43 : $pointsObtained.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        final Object $game = this.getGame();
        result = result * PRIME + ($game == null ? 43 : $game.hashCode());
        return result;
    }

    public static class UserProgressDTOBuilder {
        private Integer pointsObtained;
        private UserDTO user;
        private GameDTO game;

        UserProgressDTOBuilder() {
        }

        public UserProgressDTOBuilder pointsObtained(Integer pointsObtained) {
            this.pointsObtained = pointsObtained;
            return this;
        }

        public UserProgressDTOBuilder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public UserProgressDTOBuilder game(GameDTO game) {
            this.game = game;
            return this;
        }

        public UserProgressDTO build() {
            return new UserProgressDTO(this.pointsObtained, this.user, this.game);
        }

        public String toString() {
            return "UserProgressDTO.UserProgressDTOBuilder(pointsObtained=" + this.pointsObtained + ", user=" + this.user + ", game=" + this.game + ")";
        }
    }
}
