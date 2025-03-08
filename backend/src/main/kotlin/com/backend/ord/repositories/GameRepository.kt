package com.backend.ord.repositories

import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.repositories.bases.UserResourceRepository
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameRepository : UserResourceRepository<Game> {

    @Transactional
    @Modifying
    @Query(
        """
        UPDATE Game g
        SET g.status = 'CANCELED'
        WHERE g.id = :gameId
        AND g.userId = :userId
    """
    )
    fun cancelGame(gameId: UUID, userId: UUID): Int
}