package com.backend.ord.testing_utils.api_requests_factories

import com.backend.ord.api.requests.bank.data.CreateBankRequest
import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.data.*
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.requests.word.enums.WordToggleableProperty
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.word.model.WordEntity
import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.core.word.model.json.ExampleSentence
import com.backend.ord.testing_utils.api_requests_factories.data.CreateWordData
import com.backend.ord.testing_utils.api_requests_factories.data.UpdateWordData
import com.backend.ord.testing_utils.dto.MockedAuthenticatedUser
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.util.*

class WordRequestFactory(
    private val BASE_URL: String,
    private val objectMapper: ObjectMapper,
) {
    fun getSingleWordRequest(
        authenticatedUser: MockedAuthenticatedUser? = null,
        wordId: UUID? = null
    ): MockHttpServletRequestBuilder {
        val url = "$BASE_URL/$wordId"

        return MockMvcRequestBuilders
            .get(url)
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
    }

    fun getManyWordsRequest(
        authenticatedUser: MockedAuthenticatedUser? = null,
        language: LanguageName,

        page: Int? = null,
        perPage: Int? = null,

        wordType: WordType? = null,
        completed: Boolean? = null,
        searchingPhrase: String? = null,
        bookmarked: Boolean? = null,
        wordExtraMark: WordExtraMark? = null,

        banksIds: Set<UUID>? = null,
        bankGroupsIds: Set<UUID>? = null,

        sortDirection: SortDirection? = null,
        sortBy: GetAllWordsSortOptions? = null,
    ): MockHttpServletRequestBuilder {
        val url = "$BASE_URL/get-many-words"

        return MockMvcRequestBuilders
            .post(url)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
            .content(
                objectMapper.writeValueAsString(
                    GetManyWordsRequest(
                        language = language,

                        page = page,
                        perPage = perPage,

                        wordType = wordType,
                        completed = completed,
                        wordExtraMark = wordExtraMark,
                        bookmarked = bookmarked,
                        searchingPhrase = searchingPhrase,

                        banksIds = banksIds?.toList(),
                        bankGroupsIds = bankGroupsIds?.toList(),

                        sortBy = sortBy,
                        sortDirection = sortDirection
                    )
                )
            )
    }

    fun getManyWordsRequestUnsafe(
        authenticatedUser: MockedAuthenticatedUser? = null,
        language: Any? = null,

        page: Any? = null,
        perPage: Any? = null,

        wordType: Any? = null,
        searchingPhrase: Any? = null,
        bookmarked: Any? = null,
        completed: Any? = null,
        wordExtraMark: Any? = null,

        banksIds: Any? = null,
        bankGroupsIds: Any? = null,

        sortDirection: Any? = null,
        sortBy: Any? = null,
    ): MockHttpServletRequestBuilder {
        var url = "$BASE_URL/get-many-words"

        return MockMvcRequestBuilders
            .post(url)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
            .content(
                objectMapper.writeValueAsString(
                    UnsafeGetManyWordsRequest(
                        language = language,

                        page = page,
                        perPage = perPage,

                        wordType = wordType,
                        completed = completed,
                        wordExtraMark = wordExtraMark,
                        bookmarked = bookmarked,
                        searchingPhrase = searchingPhrase,

                        banksIds = banksIds,
                        bankGroupsIds = bankGroupsIds,

                        sortBy = sortBy,
                        sortDirection = sortDirection
                    )
                )
            )
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
        bankToCreate: CreateBankRequest? = CreateWordData.bankToCreate
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    CreateWordRequest(
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
        bankToCreate: CreateBankRequest? = UpdateWordData.bankToCreate
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.patch("$BASE_URL/$wordId")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    UpdateWordRequest(
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
    fun updateWordRequestWithNulls(
        wordId: UUID,
        authenticatedUser: MockedAuthenticatedUser? = null,

        origin: String? = null,
        definition: String? = null,
        translation: String? = null,

        type: WordType? = null,
        extraMark: WordExtraMark? = null,
        translatedTo: LanguageName? = null,
        translatedFrom: LanguageName? = null,

        useCases: Set<String>? = null,
        exampleSentences: Set<ExampleSentence>? = null,

        bankId: UUID? = null,
        bankToCreate: CreateBankRequest? = null
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.patch("$BASE_URL/$wordId")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    UpdateWordRequest(
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
     * DELETE /words/{wordId}
     */
    fun deleteWordRequestWithNulls(
        wordId: UUID,
        authenticatedUser: MockedAuthenticatedUser? = null,
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.delete("$BASE_URL/$wordId").apply {
            if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
        }
    }

    /**
     * POST /words/{wordId}/change-bank
     */
    fun changeBankForSingleWord(
        wordId: UUID,
        authenticatedUser: MockedAuthenticatedUser? = null,

        bankId: UUID? = null,
        bankToCreate: CreateBankRequest? = null,
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .post("$BASE_URL/$wordId/change-bank")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    ChangeBankForSingleWordRequest(
                        bankId = bankId,
                        bankToCreate = bankToCreate
                    )
                )
            )
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
    }

    /**
     * POST /words/change-bank-for-multiple-words
     */
    @JvmName("changeBankForMultipleWordsWithWordIdsAsWordList")
    fun changeBankForMultipleWords(
        authenticatedUser: MockedAuthenticatedUser? = null,

        wordEntityIds: List<WordEntity>,
        bankId: UUID? = null,
        bankToCreate: CreateBankRequest? = null,
    ): MockHttpServletRequestBuilder {
        return changeBankForMultipleWords(
            authenticatedUser = authenticatedUser,
            wordIds = wordEntityIds.map { it.id },
            bankId = bankId,
            bankToCreate = bankToCreate
        )
    }

    /**
     * POST /words/change-bank-for-multiple-words
     */
    @JvmName("changeBankForMultipleWordsWithWordIdsAsUUIDList")
    fun changeBankForMultipleWords(
        authenticatedUser: MockedAuthenticatedUser? = null,

        wordIds: List<UUID>,
        bankId: UUID? = null,
        bankToCreate: CreateBankRequest? = null,
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .post("$BASE_URL/change-bank-for-multiple-words")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    ChangeBankForMultipleWordsRequest(
                        wordIds = wordIds,
                        bankId = bankId,
                        bankToCreate = bankToCreate
                    )
                )
            )
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
            }
    }

    /**
     * POST /words/{wordId}/toggle-property
     */
    fun togglePropertyRequest(
        wordId: UUID,
        authenticatedUser: MockedAuthenticatedUser? = null,
        property: WordToggleableProperty? = null
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .post("$BASE_URL/$wordId/toggle-property")
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
                if (property != null) this.param("property", property.toString())
            }
    }

    /**
     * POST /words/toggle-property-for-multiple-words
     */
    fun togglePropertyForMultipleWordsRequest(
        authenticatedUser: MockedAuthenticatedUser? = null,
        wordEntities: List<WordEntity>? = null,
        property: WordToggleableProperty? = null
    ): MockHttpServletRequestBuilder {
        val wordIds = wordEntities?.map { it.id }

        return MockMvcRequestBuilders
            .post("$BASE_URL/toggle-property-for-multiple-words")
            .apply {
                if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
                if (property != null) this.param("property", property.toString())
            }
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    when (wordIds) {
                        null -> null
                        else -> WordBulkActionRequest(ids = wordIds)
                    }
                )
            )
    }

}