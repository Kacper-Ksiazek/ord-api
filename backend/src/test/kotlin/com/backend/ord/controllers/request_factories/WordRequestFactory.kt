package com.backend.ord.controllers.request_factories

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.word.data.CreateWordRequestData
import com.backend.ord.api.requests.word.data.UpdateWordRequestData
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.util.*

object UpdatedWordData {
    val origin: String = "UPDATED word in foraign language";
    val translation: String = "UPDATED word in native language";
    val definition: String = "UDPADED definition";

    val type: WordType = WordType.VERB;
    val extraMark: WordExtraMark = WordExtraMark.SLANG;
    val translatedTo: LanguageName = LanguageName.SLOVENIAN;
    val translatedFrom: LanguageName = LanguageName.NORWEGIAN;

    val useCases: Set<String> = setOf("UPDATED use case 1", "UPDATED use case 2");
    val exampleSentences: Set<ExampleSentence> = setOf(
        ExampleSentence(
            sentence = "UPDATED example sentence",
            translation = "UPDATED"
        ),
        ExampleSentence(
            sentence = "another UPDATED example sentence",
            translation = "kolejne UPDATED przykladowe zdanie"
        )
    );

    val bankId: UUID? = null;
    val bankToCreate: CreateBankRequestData? = null;
}

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
        bankToCreate: CreateBankRequestData? = null

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
        wordId: UUID,

        origin: String = UpdatedWordData.origin,
        definition: String = UpdatedWordData.definition,
        translation: String = UpdatedWordData.translation,

        type: WordType = UpdatedWordData.type,
        extraMark: WordExtraMark? = UpdatedWordData.extraMark,
        translatedTo: LanguageName? = UpdatedWordData.translatedTo,
        translatedFrom: LanguageName = UpdatedWordData.translatedFrom,

        useCases: Set<String> = UpdatedWordData.useCases,
        exampleSentences: Set<ExampleSentence> = UpdatedWordData.exampleSentences,

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
                        id = wordId,

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

    fun assertWordWasUpdated(idOfWordToUpdate: UUID, updatedWord: Word) {
        with(updatedWord) {
            id shouldBe idOfWordToUpdate

            origin shouldBe UpdatedWordData.origin
            translation shouldBe UpdatedWordData.translation
            definition shouldBe UpdatedWordData.definition

            type shouldBe UpdatedWordData.type
            extraMark shouldBe UpdatedWordData.extraMark
            translatedTo shouldBe UpdatedWordData.translatedTo
            translatedFrom shouldBe UpdatedWordData.translatedFrom

            useCases shouldBe UpdatedWordData.useCases
            exampleSentences shouldBe UpdatedWordData.exampleSentences
        }
    }
}