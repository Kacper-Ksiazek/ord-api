package com.backend.ord.domain.dto;

import java.util.UUID;

public class BanksUsedInGameDTO {
    private UUID id;

    private GameDTO game;
    private BankDTO bank;

    public BanksUsedInGameDTO(UUID id, GameDTO game, BankDTO bank) {
        this.id = id;
        this.game = game;
        this.bank = bank;
    }

    public BanksUsedInGameDTO() {
    }

    public static BanksUsedInGameDTOBuilder builder() {
        return new BanksUsedInGameDTOBuilder();
    }

    public UUID getId() {
        return this.id;
    }

    public GameDTO getGame() {
        return this.game;
    }

    public BankDTO getBank() {
        return this.bank;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setGame(GameDTO game) {
        this.game = game;
    }

    public void setBank(BankDTO bank) {
        this.bank = bank;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BanksUsedInGameDTO)) return false;
        final BanksUsedInGameDTO other = (BanksUsedInGameDTO) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$game = this.getGame();
        final Object other$game = other.getGame();
        if (this$game == null ? other$game != null : !this$game.equals(other$game)) return false;
        final Object this$bank = this.getBank();
        final Object other$bank = other.getBank();
        if (this$bank == null ? other$bank != null : !this$bank.equals(other$bank)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BanksUsedInGameDTO;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $game = this.getGame();
        result = result * PRIME + ($game == null ? 43 : $game.hashCode());
        final Object $bank = this.getBank();
        result = result * PRIME + ($bank == null ? 43 : $bank.hashCode());
        return result;
    }

    public String toString() {
        return "BanksUsedInGameDTO(id=" + this.getId() + ", game=" + this.getGame() + ", bank=" + this.getBank() + ")";
    }

    public static class BanksUsedInGameDTOBuilder {
        private UUID id;
        private GameDTO game;
        private BankDTO bank;

        BanksUsedInGameDTOBuilder() {
        }

        public BanksUsedInGameDTOBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public BanksUsedInGameDTOBuilder game(GameDTO game) {
            this.game = game;
            return this;
        }

        public BanksUsedInGameDTOBuilder bank(BankDTO bank) {
            this.bank = bank;
            return this;
        }

        public BanksUsedInGameDTO build() {
            return new BanksUsedInGameDTO(this.id, this.game, this.bank);
        }

        public String toString() {
            return "BanksUsedInGameDTO.BanksUsedInGameDTOBuilder(id=" + this.id + ", game=" + this.game + ", bank=" + this.bank + ")";
        }
    }
}
