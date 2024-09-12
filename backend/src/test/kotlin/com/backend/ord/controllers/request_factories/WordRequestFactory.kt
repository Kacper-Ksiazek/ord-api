package com.backend.ord.controllers.request_factories

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.word.data.CreateWordRequestData
import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.controllers.request_factories.data.CreateWordData
import com.backend.ord.controllers.request_factories.data.UpdateWordData
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.util.*

class WordRequestFactory(
    private val BASE_URL: String,
    private val objectMapper: ObjectMapper,
) {
    fun getSingleWordRequest() {
        // TODO
    }

    fun getAllWordsRequest() {
        // TODO
    }

    /**
     * POST /words/
     */
    fun createWordRequest(
        authenticatedUser: MockedAuthenticatedUser? = null,

        origin: String = CreateWordData.origin!!,
        translation: String = CreateWordData.translation!!,
        definition: String = CreateWordData.definition!!,

        type: WordType = CreateWordData.type!!,
        extraMark: WordExtraMark? = CreateWordData.extraMark,
        translatedTo: LanguageName? = CreateWordData.translatedTo,
        translatedFrom: LanguageName = CreateWordData.translatedFrom!!,

        useCases: Set<String> = CreateWordData.useCases!!,
        exampleSentences: Set<ExampleSentence> = CreateWordData.exampleSentences!!,

        bankId: UUID? = CreateWordData.bankId,
        bankToCreate: CreateBankRequestData? = CreateWordData.bankToCreate
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    CreateWordRequestData(
                        origin = origin,
                        translation = translation,
                        translatedFrom = translatedFrom,
                        translatedTo = translatedTo,
                        type = type,
                        exampleSentences = exampleSentences,
                        extraMark = extraMark,
                        bankId = bankId,
                        bankToCreate = bankToCreate,
                        definition = definition,
                        useCases = useCases
                    )
                )
            ).apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
    }

    /**
     * PATCH /words/{wordId}
     */
    fun updateWordRequest(
        authenticatedUser: MockedAuthenticatedUser? = null,
        wordId: UUID,

        origin: String? = UpdateWordData.origin,
        definition: String? = UpdateWordData.definition,
        translation: String? = UpdateWordData.translation,

        type: WordType? = UpdateWordData.type,
        extraMark: WordExtraMark? = UpdateWordData.extraMark,
        translatedTo: LanguageName? = UpdateWordData.translatedTo,
        translatedFrom: LanguageName? = UpdateWordData.translatedFrom,

        useCases: Set<String>? = UpdateWordData.useCases,
        exampleSentences: Set<ExampleSentence>? = UpdateWordData.exampleSentences,

        bankId: UUID? = UpdateWordData.bankId,
        bankToCreate: CreateBankRequestData? = UpdateWordData.bankToCreate
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.patch("$BASE_URL/$wordId")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    UpdateWordRequestData(
                        origin = origin,
                        translation = translation,
                        translatedFrom = translatedFrom,
                        translatedTo = translatedTo,
                        type = type,
                        exampleSentences = exampleSentences,
                        extraMark = extraMark,
                        bankId = bankId,
                        bankToCreate = bankToCreate,
                        definition = definition,
                        useCases = useCases
                    )
                )
            ).apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
    }

    fun updateWordRequestWithNulls(
        wordId: UUID,

        origin: String? = null,
        definition: String? = null,
        translation: String? = null,

        type: WordType? = null,
        extraMark: WordExtraMark? = null,
        translatedTo: LanguageName? = null,
        translatedFrom: LanguageName? = null,

        useCases: Set<String>? = null,
        exampleSentences: Set<ExampleSentence>? = null,

        authenticatedUser: MockedAuthenticatedUser? = null,
        bankId: UUID? = null,
        bankToCreate: CreateBankRequestData? = null
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.patch("$BASE_URL/$wordId")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    UpdateWordRequestData(
                        origin = origin,
                        translation = translation,
                        translatedFrom = translatedFrom,
                        translatedTo = translatedTo,
                        type = type,
                        exampleSentences = exampleSentences,
                        extraMark = extraMark,
                        bankId = bankId,
                        bankToCreate = bankToCreate,
                        definition = definition,
                        useCases = useCases
                    )
                )
            ).apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
    }
}