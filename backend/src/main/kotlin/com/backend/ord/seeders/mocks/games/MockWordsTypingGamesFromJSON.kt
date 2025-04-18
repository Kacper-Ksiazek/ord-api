package com.backend.ord.seeders.mocks.games

import com.backend.ord.domain.application.games.words_typing.WordsTypingInstruction
import com.backend.ord.domain.persistence.dto.OngoingWordsTypingGameDTO
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.seeders.mocks.bases.ResourceJSONFileReader
import com.backend.ord.seeders.mocks.games.json_data_models.WordTypingGameInJSON
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component

@Component
class MockWordsTypingGamesFromJSON(
    val userMapper: UserMapper
) : ResourceJSONFileReader<
        List<WordTypingGameInJSON>,
        WordTypingGameInJSON
        > {

    override fun typeReference(): TypeReference<List<WordTypingGameInJSON>> {
        return object : TypeReference<List<WordTypingGameInJSON>>() {}
    }

    override val pathToJSONFile: String = "/games/crosswords.json"

    fun seedFromJSONFile(
        user: User
    ): List<Pair<OngoingWordsTypingGameDTO, WordsTypingInstruction>> {
        val jsonData: List<WordTypingGameInJSON> = readFromJSONFile()

        val userDTO = userMapper.toDTO(user)

        return jsonData.map {
            val ongoingGameDTO = OngoingWordsTypingGameDTO(
                properAnswers = it.properAnswers,
                type = GameType.WORDS_TYPING,
                language = it.language,
                difficulty = it.difficulty,
                user = userDTO
            )

            return@map Pair(ongoingGameDTO, it.instruction)
        }
    }
}