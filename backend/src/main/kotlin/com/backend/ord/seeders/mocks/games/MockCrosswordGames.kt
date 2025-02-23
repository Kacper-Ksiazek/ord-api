package com.backend.ord.seeders.mocks.games

import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.mappers.GameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.repositories.GameRepository
import com.backend.ord.seeders.mocks._bases.MocksFromJsonFileHandler
import com.backend.ord.seeders.mocks.games.json_data_models.CrosswordInJSON
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component

@Component
class MockCrosswordGames(
    override val repository: GameRepository,
    val gameMapper: GameMapper,
    val userMapper: UserMapper
) : MocksFromJsonFileHandler<
        Game,
        List<CrosswordInJSON>,
        CrosswordInJSON
        > {

    override fun typeReference(): TypeReference<List<CrosswordInJSON>> {
        return object : TypeReference<List<CrosswordInJSON>>() {}
    }

    override val pathToJSONFile: String = "/games/crosswords.json"

    override fun convertToEntity(
        jsonData: CrosswordInJSON,
        user: User
    ): Game {
        return gameMapper.toEntity(
            CrosswordGameDTO(
                instruction = jsonData.instruction,
                properAnswers = jsonData.properAnswers,
                language = jsonData.language,
                difficulty = jsonData.difficulty,
                user = userMapper.toDTO(user)
            )
        )
    }
}