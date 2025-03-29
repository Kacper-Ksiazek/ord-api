package com.backend.ord.seeders.mocks.games

import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.seeders.mocks.bases.ResourceJSONFileReader
import com.backend.ord.seeders.mocks.games.json_data_models.CrosswordInJSON
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component

@Component
class MockCrosswordGames(
    val userMapper: UserMapper
) : ResourceJSONFileReader<
        List<CrosswordInJSON>,
        CrosswordInJSON
        > {

    override fun typeReference(): TypeReference<List<CrosswordInJSON>> {
        return object : TypeReference<List<CrosswordInJSON>>() {}
    }

    override val pathToJSONFile: String = "/games/crosswords.json"

    fun seedFromJSONFile(
        user: User
    ): List<Pair<OngoingCrosswordGameDTO, CrosswordInstruction>> {
        val jsonData: List<CrosswordInJSON> = readFromJSONFile()

        val userDTO = userMapper.toDTO(user)

        return jsonData.map {
            val ongoingGameDTO = OngoingCrosswordGameDTO(
                properAnswers = it.properAnswers,
                type = GameType.CROSSWORD,
                language = it.language,
                difficulty = it.difficulty,
                user = userDTO
            )

            return@map Pair(ongoingGameDTO, it.instruction)
        }
    }
}