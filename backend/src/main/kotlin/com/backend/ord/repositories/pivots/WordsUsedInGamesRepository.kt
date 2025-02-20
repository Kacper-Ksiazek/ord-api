package com.backend.ord.repositories.pivots

import com.backend.ord.domain.persistence.entities.pivots.WordUsedInGame
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WordsUsedInGamesRepository : JpaRepository<WordUsedInGame, UUID> {
    @Query(
        """
            SELECT w FROM WordUsedInGame w 
            WHERE 
                w.gameId = :gameId
            """
    )
    fun findAllByGameId(gameId: UUID): List<WordUsedInGame>
}
