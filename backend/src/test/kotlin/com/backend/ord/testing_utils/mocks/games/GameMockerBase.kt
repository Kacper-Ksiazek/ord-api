package com.backend.ord.testing_utils.mocks.games

import com.backend.ord.api.responses.games.bases.StartedGameResponse
import com.backend.ord.controllers.bases.ControllerTestBase
import com.backend.ord.domain.persistence.dto.OngoingGameDTO
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.repositories.OngoingGameRepository
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.factories.WordMockFactory
import com.backend.ord.seeders.mocks.bases.ResourceJSONFileReader
import com.backend.ord.seeders.mocks.bases.RootDir
import com.backend.ord.testing_utils.api_requests_factories.GameRequestFactory
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser
import com.backend.ord.testing_utils.dto.resources.mocks.GameInJson
import com.backend.ord.utils.resource_readers.loadWordsFromResourceFile
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc

interface GameMockerBase<
        TJSONDataModelType : GameInJson<TGameInstruction, *>,        // eg. CrosswordInJson
        TOngoingGameDTO : OngoingGameDTO<*>,                         // eg. OngoingCrosswordGameDTO
        TGameInstruction,                                            // eg. CrosswordInstruction
        TAPIResponseDTO : StartedGameResponse<TGameInstruction, *>   // eg. StartedCrosswordGameResponse
        > : ResourceJSONFileReader<List<TJSONDataModelType>, TJSONDataModelType> {
    override val root: RootDir
        get() = RootDir.TEST_RESOURCES

    // Properties
    val mockingGameType: GameType

    // Class references:
    val apiResponseTypeRef: TypeReference<TAPIResponseDTO>

    // Dependencies:
    val mockMvc: MockMvc
    val userMapper: UserMapper
    val objectMapper: ObjectMapper
    val wordRepository: WordRepository
    val wordMockFactory: WordMockFactory
    val ongoingGameMapper: OngoingGameMapper
    val gameRequestFactory: GameRequestFactory
    val ongoingGameRepository: OngoingGameRepository

    // Static
    companion object {
        object DefaultParams {
            val language: LanguageName = LanguageName.ENGLISH
            val difficulty: GameDifficulty = GameDifficulty.HARD
        }
    }

    /**
     * **FAST way** of mocking an ongoing game.
     * Loads predefined data from JSON file, saves it to the database and returns it.
     */
    fun mockFromJsonSource(
        userDTO: UserDTO,
        difficulty: GameDifficulty = GameDifficulty.HARD
    ): Pair<TOngoingGameDTO, TGameInstruction> {
        val (ongoingGameDTO, instruction) = loadDataFromJsonFile(userDTO)
            .filter { it.first.difficulty == difficulty }
            .random()
            .let {
                val savedOngoingGame = ongoingGameRepository.save(
                    ongoingGameMapper.toEntity(it.first)
                )

                val updatedDTO = it.first.copy(id = savedOngoingGame.id)

                @Suppress("UNCHECKED_CAST")
                Pair(updatedDTO as TOngoingGameDTO, it.second)
            }

        val currentWords = wordRepository
            .findAllForUser(userDTO.id)
            .filter { it.translatedFrom == ongoingGameDTO.language }
            .map { it.origin }

        wordRepository.saveAll(
            getListOfUsedWords(ongoingGameDTO)
                .filter { it !in currentWords }
                .map {
                    wordMockFactory.mockEntity(
                        origin = it,
                        translatedFrom = ongoingGameDTO.language,
                        user = userMapper.toEntity(userDTO),
                    )
                }
        )

        return Pair(ongoingGameDTO, instruction)
    }

    /**
     * **SLOW way** of mocking an ongoing game.
     * Makes a proper API call to the server, waits for AI to generate an actual game,
     * saves it to the database and returns it.
     */
    fun mockThroughApiFlow(
        authenticatedUser: MockedAuthenticatedUser,
        difficulty: GameDifficulty = DefaultParams.difficulty,
        language: LanguageName = DefaultParams.language
    ): Pair<TOngoingGameDTO, TAPIResponseDTO> {
        loadWordsFromResourceFile(
            user = userMapper.toEntity(authenticatedUser.userInfo),
            wordsRepository = wordRepository
        )

        val request = gameRequestFactory.startGameRequest(
            gameType = mockingGameType,
            language = language,
            difficulty = difficulty,
            authenticatedUser = authenticatedUser
        )
        val response = mockMvc.perform(request).andReturn().let {
            it.response.status shouldBe HttpStatus.OK.value()
            it.response
        }

        val apiResponseBody = ControllerTestBase.Companion.getResponseBody(
            objectMapper,
            response,
            typeReference = apiResponseTypeRef
        )

        @Suppress("UNCHECKED_CAST") val ongoingGameDTO = ongoingGameMapper.toDTO(
            entity = ongoingGameRepository.findByIdOrNull(apiResponseBody.gameId)!!,
        ) as TOngoingGameDTO

        return Pair(
            ongoingGameDTO,
            apiResponseBody
        )
    }

    /**
     * Utility function to process the JSON row into an ongoing game DTO.
     */
    fun createOngoingGameDTO(
        jsonData: TJSONDataModelType,
        userDTO: UserDTO
    ): TOngoingGameDTO

    /**
     * Utility function to extract the list of used words from the ongoing game DTO.
     */
    fun getListOfUsedWords(ongoingGameDTO: TOngoingGameDTO): Set<String>

    /**
     * Reads the JSON file, process its content and returns a list of pairs
     * of ongoing game DTOs and their instructions.
     */
    fun loadDataFromJsonFile(
        userDTO: UserDTO
    ): List<Pair<TOngoingGameDTO, TGameInstruction>> {
        val jsonData: List<TJSONDataModelType> = readFromJSONFile()

        return jsonData.map {
            Pair(
                createOngoingGameDTO(it, userDTO),
                it.instruction
            )
        }
    }
}