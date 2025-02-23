package com.backend.ord.seeders.factories

import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.utils.EnumUtils.getRandomValue
import org.springframework.stereotype.Component

@Component
class GameMockFactory(
    private val userSeeder: UserSeeder
) : AbstractFactory() {

    fun mockEntity(
        type: GameType,
        language: LanguageName,

        user: User = userSeeder.seedOneEntity(),
        status: GameStatus = GameStatus.IN_PROGRESS,
    ): Game {
        return Game(
            type = type,
            status = status,
            language = language,

            instruction = faker.lorem().sentence(),
            properAnswers = faker.lorem().sentence(),
            difficulty = GameDifficulty::class.getRandomValue(),

            user = user
        )
    }
}