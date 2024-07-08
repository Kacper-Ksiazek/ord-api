package com.backend.ord.domain.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "banks_used_in_games")
public class BanksUsedInGame {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    public UUID getId() {
        return this.id;
    }

    public Game getGame() {
        return this.game;
    }

    public Bank getBank() {
        return this.bank;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}