package com.backend.ord.domain.persistence.entities.pivots

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "words_used_in_games")
data class WordUsedInGame(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "game_id")
    val gameId: UUID,

    @Column(name = "word_id")
    val wordId: UUID
)