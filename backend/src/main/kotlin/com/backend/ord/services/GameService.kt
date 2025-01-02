package com.backend.ord.services

import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.services.bases.UserResourceService
import java.util.*

interface GameService : UserResourceService<Game> {
    fun finishGame(
        game: Game,
        finalScore: Int,
        duration: String
    ): Game

    /**
     * Creates records in a pivot table called `words_used_in_games` for
     * each of the word used in a game with a given ID
     */
    fun saveAllWordsUsedInAGame(
        wordsIds: Set<UUID>,
        gameId: UUID
    )
}