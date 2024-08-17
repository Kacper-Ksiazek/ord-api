package com.backend.ord.controllers.request_factories

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.api.requests.word.CreateWordRequest
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
        origin: String = "word in english",
        translation: String = "slowo po polsku",
        translatedFrom: LanguageName = LanguageName.ENGLISH,
        translatedTo: LanguageName? = LanguageName.POLISH,
        type: WordType = WordType.NOUN,
        definition: String = "definition",
        useCases: Set<String> = setOf("use case 1", "use case 2"),
        exampleSentences: Set<ExampleSentence> = setOf(
            ExampleSentence(
                sentence = "example sentence",
                translation = "przykladowe zdanie"
            ),
            ExampleSentence(
                sentence = "another example sentence",
                translation = "kolejne przykladowe zdanie"
            )
        ),
        authenticatedUser: MockedAuthenticatedUser? = null,
        extraMark: WordExtraMark? = WordExtraMark.SLANG,
        bankId: UUID? = null,
        bankToCreate: CreateBankRequest? = null

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
}