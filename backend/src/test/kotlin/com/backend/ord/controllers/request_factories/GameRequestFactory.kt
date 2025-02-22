package com.backend.ord.controllers.request_factories

import com.backend.ord.api.requests.games.data.UnsafeStartGameRequestData
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

private fun GameType.getStartGameAPIUrl(): String {
    return "/api/v1/games/${this.name.lowercase()}/start"
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

}