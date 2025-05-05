package com.backend.ord.testing_utils.api_requests_factories

import com.backend.ord.api.requests.games.CancelGameRequest
import com.backend.ord.api.requests.games.UnsafeFinishGameRequestData
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

private fun GameType.getSlugName(): String {
    return when (this) {
        GameType.CROSSWORD -> "crossword"
        GameType.WORDS_TYPING -> "words-typing"
        else -> throw UnsupportedOperationException()
    }
}

private fun GameType.getStartGameAPIUrl(): String {
    return "/api/v1/games/${this.getSlugName()}/start"
}

private fun GameType.getFinishGameAPIUrl(): String {
    return "/api/v1/games/${this.getSlugName()}/finish"
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

    fun <TUserAnswers> finishGameRequest(
        gameType: GameType,
        authenticatedUser: MockedAuthenticatedUser? = null,
        gameId: UUID? = null,
        duration: String? = "02:30:00",
        answers: TUserAnswers? = null,
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .post(gameType.getFinishGameAPIUrl())
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    UnsafeFinishGameRequestData(
                        gameId = gameId,
                        duration = duration,
                        answers = answers
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