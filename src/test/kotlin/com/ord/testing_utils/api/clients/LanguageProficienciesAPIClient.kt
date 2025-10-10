package com.ord.testing_utils.api.clients

import com.ord.core.langugae_proficiency.api.requests.CreateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.api.requests.UpdateLanguageProficiencyRequest
import com.ord.core.langugae_proficiency.model.LanguageProficiencyDTO
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

class LanguageProficienciesAPIClient(
    webClient: WebTestClient
) : APITestClient(webClient) {
    val baseUrl = "/api/v1/language-proficiencies"

    fun getLanguagesForUser(
        user: MockedAuthenticatedUser? = null
    ): APIClientResponse<Map<LanguageName, LanguageProficiencyLevel>?> {
        return get(
            url = baseUrl,
            user = user,
            responseBodyType = object : ParameterizedTypeReference<Map<LanguageName, LanguageProficiencyLevel>>() {}
        )
    }

    fun createLanguageProficiency(
        user: MockedAuthenticatedUser? = null,
        body: CreateLanguageProficiencyRequest
    ): APIClientResponse<LanguageProficiencyDTO?> {
        return post(
            url = baseUrl,
            user = user,
            body = body,
            responseBodyType = object : ParameterizedTypeReference<LanguageProficiencyDTO>() {}
        )
    }

    fun updateLanguageProficiency(
        user: MockedAuthenticatedUser? = null,
        body: UpdateLanguageProficiencyRequest
    ): APIClientResponse<LanguageProficiencyDTO?> {
        return patch(
            url = baseUrl,
            user = user,
            body = body,
            responseBodyType = object : ParameterizedTypeReference<LanguageProficiencyDTO>() {}
        )
    }

    fun deleteLanguageProficiency(
        user: MockedAuthenticatedUser? = null,
        language: LanguageName
    ): APIClientResponse<Unit?> {
        return delete(
            url = "$baseUrl/$language",
            user = user
        )
    }
}
