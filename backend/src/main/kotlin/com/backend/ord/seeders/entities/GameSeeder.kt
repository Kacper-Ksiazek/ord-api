package com.backend.ord.seeders.entities

import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.repositories.GameRepository
import com.backend.ord.seeders.factories.GameMockFactory
import org.springframework.stereotype.Component

@Component
class GameSeeder(
    private val gameRepository: GameRepository,
    private val gameMockFactory: GameMockFactory,
    private val userSeeder: UserSeeder
) : SeederInterface<Game> {
    fun seedOneEntity(
        type: GameType,
        language: LanguageName,
        user: User = userSeeder.seedOneEntity(),
        status: GameStatus = GameStatus.IN_PROGRESS,
    ): Game {
        return gameRepository.save(
            gameMockFactory.mockEntity(
                type = type,
                language = language,
                user = user,
                status = status
            )
        )
    }

    override fun deleteAll() {
        gameRepository.deleteAll()
    }
}