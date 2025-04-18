package com.backend.ord.seeders.mocks.games

import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.repositories.OngoingGameRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.seeders.mocks.bases.ResourceJSONFileReader
import com.backend.ord.seeders.mocks.games.json_data_models.CrosswordInJSON
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component

@Component
class MockCrosswordGamesFromJSON(
    val userMapper: UserMapper,
    val wordRepository: WordRepository,
    val ongoingGameMapper: OngoingGameMapper,
    val ongoingGameRepository: OngoingGameRepository,
    val wordMockFactory: WordMockFactory,
) : ResourceJSONFileReader<
        List<CrosswordInJSON>,
        CrosswordInJSON
        > {
    override val pathToJSONFile: String = "/games/crosswords.json"

    override fun typeReference(): TypeReference<List<CrosswordInJSON>> {
        return object : TypeReference<List<CrosswordInJSON>>() {}
    }

    fun createMockFromJSON(
        user: UserDTO,
        difficulty: GameDifficulty = GameDifficulty.HARD
    ): Pair<OngoingCrosswordGameDTO, CrosswordInstruction> {
        val (crosswordSavedInDb, crosswordInstruction) = parseJSONFileData(user)
            .filter { it.first.difficulty == difficulty }
            .random()
            .let {
                val savedOngoingGame = ongoingGameRepository.save(
                    ongoingGameMapper.toEntity(it.first)
                )
                // TODO: Come up with something smarter for this, eg. involving @PrePersist annotation of hybernate
                val updatedDTO = it.first.copy(id = savedOngoingGame.id)

                Pair(updatedDTO, it.second)
            }

        wordRepository.saveAll(
            crosswordSavedInDb.properAnswers.questions.values.map {
                wordMockFactory.mockEntity(
                    origin = it,
                    translatedFrom = crosswordSavedInDb.language,
                    user = userMapper.toEntity(user),
                )
            }
        )

        return Pair(crosswordSavedInDb, crosswordInstruction)
    }


    private fun parseJSONFileData(
        userDTO: UserDTO
    ): List<Pair<OngoingCrosswordGameDTO, CrosswordInstruction>> {
        val jsonData: List<CrosswordInJSON> = readFromJSONFile()

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