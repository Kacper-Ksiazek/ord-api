package com.backend.ord.testing_utils.api_requests_factories

import com.backend.ord.api.requests.games.CancelGameRequest
import com.backend.ord.api.requests.games.CrosswordUserAnswers
import com.backend.ord.api.requests.games.UnsafeFinishCrosswordGameRequestData
import com.backend.ord.api.requests.games.UnsafeStartGameRequestData
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.util.*

private fun GameType.getStartGameAPIUrl(): String {
    return "/api/v1/games/${this.name.lowercase().replace('_', '-')}/start"
}

class GameRequestFactory(
    private val objectMapper: ObjectMapper,
) {

    fun startGameRequest(
        gameType: GameType,
        difficulty: GameDifficulty? = GameDifficulty.HARD,
        language: LanguageName? = LanguageName.ENGLISH,
        authenticatedUser: MockedAuthenticatedUser? = null,
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .post(gameType.getStartGameAPIUrl())
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    UnsafeStartGameRequestData(
                        difficulty = difficulty,
                        language = language
                    )
                )
            )
    }

    fun finishCrosswordGameRequest(
        authenticatedUser: MockedAuthenticatedUser? = null,
        gameId: UUID? = null,
        duration: String? = "02:30:00",
        userAnswers: CrosswordUserAnswers? = null,
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .post("/api/v1/games/crossword/finish")
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    UnsafeFinishCrosswordGameRequestData(
                        gameId = gameId,
                        duration = duration,
                        userAnswers = userAnswers
                    )
                )
            )
    }

    fun cancelGameRequest(
        authenticatedUser: MockedAuthenticatedUser? = null,
        gameId: UUID? = null,
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .delete("/api/v1/games/cancel/$gameId")
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    CancelGameRequest(
                        duration = "00:30:10"
                    )
                )
            )
    }
}