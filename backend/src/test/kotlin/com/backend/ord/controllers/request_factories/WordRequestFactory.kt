package com.backend.ord.controllers.request_factories

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.word.data.ChangeBankForMultipleWordsRequestData
import com.backend.ord.api.requests.word.data.ChangeBankForSingleWordRequestData
import com.backend.ord.api.requests.word.data.CreateWordRequestData
import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.controllers.request_factories.data.CreateWordData
import com.backend.ord.controllers.request_factories.data.UpdateWordData
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.domain.entities.Word
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

    fun getManyWordsRequest(
        authenticatedUser: MockedAuthenticatedUser? = null,
        language: LanguageName? = null,

        page: Int? = null,
        perPage: Int? = null,

        wordType: WordType? = null,
        searchingPhrase: String? = null,
        bookmarkedOnly: Boolean? = null,
        wordExtraMark: WordExtraMark? = null,

        banksIds: Set<UUID>? = null,
        banksGroupsIds: Set<UUID>? = null,

        sortDirection: SortDirection? = null,
        sortBy: GetAllWordsSortOptions? = null,
    ): MockHttpServletRequestBuilder {
        // Create a url and add params to it
        var url = "$BASE_URL?"

        if (language != null) url += "language=$language&"

        // Assign pagination properties
        if (page != null) url += "page=$page&"
        if (perPage != null) url += "perPage=$perPage&"

        // Assign optional predicates of non list type
        if (wordType != null) url += "wordType=$wordType&"
        if (searchingPhrase != null) url += "searchingPhrase=$searchingPhrase&"
        if (bookmarkedOnly != null) url += "bookmarkedOnly=$bookmarkedOnly&"
        if (wordExtraMark != null) url += "wordExtraMark=$wordExtraMark&"

        // Assign optional predicates of list type
        if (banksIds != null) url += "banksIds=${banksIds.joinToString(",")}&"
        if (banksGroupsIds != null) url += "banksGroupsIds=${banksGroupsIds.joinToString(",")}&"

        // Assign optional sorting
        if (sortDirection != null) url += "sortDirection=$sortDirection&"
        if (sortBy != null) url += "sortBy=$sortBy&"


        return MockMvcRequestBuilders.get(url).apply {
            if (authenticatedUser != null) this.cookie(authenticatedUser.authCookie)
        }
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

}