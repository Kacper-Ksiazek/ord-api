package com.backend.ord.controllers

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.request_factories.WordRequestFactory
import com.backend.ord.controllers.utils_for_testing.ControllerTestBase
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.entities.BankSeeder
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.seeders.entities.WordSeeder
import com.backend.ord.seeders.factories.BankMockFactory
import com.backend.ord.services.BankService
import com.backend.ord.services.WordService
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class TestWordsController @Autowired constructor(
    mockMvc: MockMvc?,
    objectMapper: ObjectMapper,
    jwtProperties: JwtProperties,
    private val wordService: WordService,
    private val wordRepository: WordRepository,
    private val bankSeeder: BankSeeder,
    private val bankMockFactory: BankMockFactory,
    private val bankService: BankService,
    private val userSeeder: UserSeeder,
    private val wordSeeder: WordSeeder,
) : ControllerTestBase(mockMvc!!, objectMapper, jwtProperties) {
    private val BASE_URL = "/api/v1/words/"

    private val wordRequestFactory = WordRequestFactory(
        BASE_URL = BASE_URL,
        objectMapper = objectMapper
    )

    @Test
    fun `Anonymous user cannot create a word`() {
        val request = wordRequestFactory.createWordRequest()

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        )
    }

    @Test
    fun `A word can be created without bank being specified`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser
        )

        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated()
        ).andReturn().response

        assertThatWordActuallyExists(response, authenticatedUser)
    }


    @Test
    fun `A word can be created and assigned to an existing bank`() {
        val authenticatedUser = mockAuthenticatedUser()

        val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            bankId = bank.id
        )

        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated()
        ).andReturn().response

        val word: Word = assertThatWordActuallyExists(response, authenticatedUser)

        assertEquals(bank.id, word.bank?.id)
    }

    @Test
    fun `A word and a bank can be created at the same time`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            bankToCreate = bankMockFactory.mockCreateRequest()
        )

        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated()
        ).andReturn().response

        val word: Word = assertThatWordActuallyExists(response, authenticatedUser)

        val bank: Bank = assertThatBankActuallyExists(word.bank)
    }

    @Test
    fun `A word can be create with no extra mark specified`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            extraMark = null
        )

        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated()
        ).andReturn().response

        assertThatWordActuallyExists(response, authenticatedUser)
    }

    @Test
    fun `A word cannot be created without example sentences`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            exampleSentences = emptySet()
        )

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        )
    }

    @Test
    fun `A word can be created with no translated to language specified defaulting to the user's native language`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            translatedTo = null
        )

        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated()
        ).andReturn().response

        val word: Word = assertThatWordActuallyExists(response, authenticatedUser)

        assertEquals(authenticatedUser.userInfo.nativeLanguage, word.translatedTo)
    }

    @Test
    fun `A word cannot be created with more than 5 example sentences`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            exampleSentences = mutableSetOf<ExampleSentence>().apply {
                repeat(6) { index ->
                    add(
                        ExampleSentence(
                            sentence = "example sentence - $index",
                            translation = "przykladowe zdanie"
                        )
                    )
                }
            }
        )

        mockMvc.perform(request).andDo { it -> println(it) }.andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        )
    }

    @Test
    fun `A word cannot be created with an example sentence that has more than 255 characters`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            exampleSentences = mutableSetOf(
                ExampleSentence(
                    sentence = "a".repeat(256),
                    translation = "przykladowe zdanie"
                )
            )
        )

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        )
    }

    @Test
    fun `A word cannot be created with bankId and bankToCreate at the same time`() {
        val authenticatedUser = mockAuthenticatedUser()

        val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            bankId = bank.id,
            bankToCreate = bankMockFactory.mockCreateRequest()
        )

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        )
    }

    @Test
    fun `A word cannot be created with bankToCreate name matching already existing bank name`() {
        val authenticatedUser = mockAuthenticatedUser()

        val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            bankToCreate = bankMockFactory.mockCreateRequest(
                name = bank.name
            )
        )

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        )
    }

    @Test
    fun `A word cannot be created with bankId referring to a bank of another user`() {
        val authenticatedUser = mockAuthenticatedUser()
        val anotherUser = userSeeder.seedOneEntity()

        val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            bankId = bankOfAnotherUser.id
        )

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        )
    }

    @Test
    fun `A word cannot be created with no use cases`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            useCases = emptySet()
        )

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        )
    }

    @Test
    fun `A word cannot be created with an example sentence that has empty translation`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            exampleSentences = mutableSetOf(
                ExampleSentence(
                    sentence = "example sentence",
                    translation = ""
                )
            )
        )

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        )
    }

    @Test
    fun `A word cannot be created with use case of length greater than 255 characters`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            useCases = mutableSetOf(
                "a".repeat(256)
            )
        )

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        )
    }

    @Test
    fun `A word cannot be created with more than 5 use cases`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            useCases = mutableSetOf<String>().apply {
                repeat(6) { index ->
                    add("use case - $index")
                }
            }
        )

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isBadRequest()
        )
    }

    @Test
    fun `A word can be created even with bank name identical to another bank name but for different user`() {
        val authenticatedUser = mockAuthenticatedUser()
        val anotherUser = userSeeder.seedOneEntity()

        val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            bankToCreate = bankMockFactory.mockCreateRequest(
                name = bankOfAnotherUser.name
            )
        )

        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated()
        ).andReturn().response

        val word: Word = assertThatWordActuallyExists(response, authenticatedUser)

        val bank: Bank = assertThatBankActuallyExists(word.bank)
    }

    // -------
    // Update
    // -------

    @Test
    fun `A word can be updated`() {
        val authenticatedUser = mockAuthenticatedUser()

        val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

        val request = wordRequestFactory.updateWordRequest(
            wordId = word.id,
            authenticatedUser = authenticatedUser,
            origin = "updated origin",
            translatedTo = LanguageName.NORWEGIAN,
            translatedFrom = LanguageName.POLISH,
            type = WordType.VERB,
            translation = "updated translation",
            extraMark = WordExtraMark.SLANG,
            exampleSentences = setOf(
                ExampleSentence(
                    sentence = "updated example sentence",
                    translation = "przykladowe zdanie"
                ),
                ExampleSentence(
                    sentence = "another updated example sentence",
                    translation = "kolejne przykladowe zdanie"
                )
            ),
            definition = "updated definition",
            useCases = setOf("updated use case 1", "updated use case 2"),
        )

        val response = mockMvc.perform(request)
            .andExpect { status().isOk }
            .andReturn().response

        val wordUpdated: Word = assertThatWordActuallyExists(response, authenticatedUser)

        with(wordUpdated) {
            id shouldBe word.id
            origin shouldBe "updated origin"
            translatedTo shouldBe LanguageName.NORWEGIAN
            translatedFrom shouldBe LanguageName.POLISH
            type shouldBe WordType.VERB
            translation shouldBe "updated translation"
            extraMark shouldBe WordExtraMark.SLANG
            definition shouldBe "updated definition"
            useCases shouldBe setOf("updated use case 1", "updated use case 2")
            exampleSentences shouldBe setOf(
                ExampleSentence(
                    sentence = "updated example sentence",
                    translation = "przykladowe zdanie"
                ),
                ExampleSentence(
                    sentence = "another updated example sentence",
                    translation = "kolejne przykladowe zdanie"
                )
            )
        }
    }

    private fun assertThatWordActuallyExists(
        response: MockHttpServletResponse,
        authenticatedUser: MockedAuthenticatedUser
    ): Word {
        val responseBody: WordDTO = getResponseBody<WordDTO>(response)
        assertNotNull(responseBody.id)

        assertEquals(authenticatedUser.userInfo.id, responseBody.user.id)

        val valueSavedInDatabase = wordRepository.findByIdOrNull(responseBody.id)

        assertNotNull(valueSavedInDatabase)

        return valueSavedInDatabase!!
    }

    private fun assertThatBankActuallyExists(
        bankToVerify: Bank?,
    ): Bank {
        assertNotNull(bankToVerify)

        return bankService.findById(id = bankToVerify!!.id).let {
            assertNotNull(it)
            it!!
        }
    }
}