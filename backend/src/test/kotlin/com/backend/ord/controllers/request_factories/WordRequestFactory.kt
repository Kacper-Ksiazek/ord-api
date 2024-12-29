package com.backend.ord.controllers.request_factories

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.data.*
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.api.requests.word.enums.WordToggleableProperty
import com.backend.ord.controllers.request_factories.data.CreateWordData
import com.backend.ord.controllers.request_factories.data.UpdateWordData
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.persistance.embedded.ExampleSentence
import com.backend.ord.domain.persistance.entities.Word
import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.enums.persistance.word.WordExtraMark
import com.backend.ord.enums.persistance.word.WordType
import com.backend.ord.unsage_api_requests.UnsafeGetManyWordsRequestData
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
                    GetManyWordsRequestData(
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
                    UnsafeGetManyWordsRequestData(
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
        bankToCreate: CreateBankRequestData? = null,
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .post("$BASE_URL/$wordId/change-bank")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    ChangeBankForSingleWordRequestData(
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

        wordIds: List<Word>,
        bankId: UUID? = null,
        bankToCreate: CreateBankRequestData? = null,
    ): MockHttpServletRequestBuilder {
        return changeBankForMultipleWords(
            authenticatedUser = authenticatedUser,
            wordIds = wordIds.map { it.id },
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
        bankToCreate: CreateBankRequestData? = null,
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders
            .post("$BASE_URL/change-bank-for-multiple-words")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    ChangeBankForMultipleWordsRequestData(
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
        words: List<Word>? = null,
        property: WordToggleableProperty? = null
    ): MockHttpServletRequestBuilder {
        val wordIds = words?.map { it.id }

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
                        else -> WordBulkActionRequestData(ids = wordIds)
                    }
                )
            )
    }

}