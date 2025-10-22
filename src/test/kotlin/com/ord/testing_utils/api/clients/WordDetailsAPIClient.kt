package com.ord.testing_utils.api.clients

import com.ord.core.word.api.details.requests.dto.CreateWordDetailsRequest
import com.ord.core.word.api.details.requests.dto.UpdateWordDetailsRequest
import com.ord.core.word.models.word_details.WordDetailsCompactDTO
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

class WordDetailsAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {

    fun createWordDetails(
        wordId: UUID,
        body: CreateWordDetailsRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<WordDetailsCompactDTO?> {
        return post(
            url = "/api/v1/words/$wordId/details",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<WordDetailsCompactDTO>() {}
        )
    }

    fun updateWordDetails(
        wordId: UUID,
        body: UpdateWordDetailsRequest,
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<WordDetailsCompactDTO?> {
        return patch(
            url = "/api/v1/words/$wordId/details",
            body = body,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<WordDetailsCompactDTO>() {}
        )
    }
}