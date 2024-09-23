package com.backend.ord.controllers

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.extensions.compareWith
import com.backend.ord.controllers.extensions.detectChanges
import com.backend.ord.controllers.request_factories.WordRequestFactory
import com.backend.ord.controllers.request_factories.data.WordDataChanges
import com.backend.ord.controllers.request_factories.data.compareWithDefaultCreateWordData
import com.backend.ord.controllers.request_factories.data.compareWithDefaultUpdateWordData
import com.backend.ord.controllers.utils_for_testing.ControllerTestBase
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.domain.mappers.WordMapper
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.entities.BankSeeder
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.seeders.entities.WordSeeder
import com.backend.ord.seeders.factories.BankMockFactory
import com.backend.ord.services.BankService
import com.backend.ord.services.WordService
import com.backend.ord.utils.Optional
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import java.util.*

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@DisplayName("WordsController - tests")
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
    private val wordMapper: WordMapper
) : ControllerTestBase(mockMvc!!, objectMapper, jwtProperties) {
    private val BASE_URL = "/api/v1/words/"

    private val wordRequestFactory = WordRequestFactory(
        BASE_URL = BASE_URL,
        objectMapper = objectMapper
    )

    @Nested
    @DisplayName("[POST] /api/v1/words/ - create a word")
    inner class CreateWordTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - Word can be created without bank being specified`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.CREATED.value()
                    it.response
                }

                val createdWord = assertThatWordActuallyExists(response, authenticatedUser)

                createdWord.compareWithDefaultCreateWordData()
            }


            @Test
            fun `201 - Word can be created and assigned to an existing bank`() {
                val authenticatedUser = mockAuthenticatedUser()

                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    bankId = bank.id
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.CREATED.value()
                    it.response
                }

                val word: Word = assertThatWordActuallyExists(response, authenticatedUser)

                word.compareWithDefaultCreateWordData()
                assertEquals(bank.id, word.bank?.id)
            }

            @Test
            fun `201 - Word and a bank can be created at the same time`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    bankToCreate = bankMockFactory.mockCreateRequestData()
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.CREATED.value()
                    it.response
                }

                val word: Word = assertThatWordActuallyExists(response, authenticatedUser)

                val bank: Bank = assertThatBankActuallyExists(word.bank)

                word.compareWithDefaultCreateWordData()
            }

            @Test
            fun `201 - Word can be create with no extra mark specified`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    extraMark = null
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.CREATED.value()
                    it.response
                }

                val createdWord = assertThatWordActuallyExists(response, authenticatedUser)

                createdWord.compareWithDefaultCreateWordData(
                    differences = WordDataChanges(
                        extraMark = Optional(null, true)
                    )
                )
            }

            @Test
            fun `201 - Word can be created with no translated to language specified defaulting to the user's native language`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    translatedTo = null
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.CREATED.value()
                    it.response
                }

                val word: Word = assertThatWordActuallyExists(response, authenticatedUser)

                word.compareWithDefaultCreateWordData(
                    differences = WordDataChanges(
                        translatedTo = Optional(authenticatedUser.userInfo.nativeLanguage)
                    )
                )
            }

            @Test
            fun `201 - Word can be created even with bank name identical to another bank name but for different user`() {
                val authenticatedUser = mockAuthenticatedUser()
                val anotherUser = userSeeder.seedOneEntity()

                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = bankOfAnotherUser.name
                    )
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.CREATED.value()
                    it.response
                }

                val word: Word = assertThatWordActuallyExists(response, authenticatedUser)

                val bank: Bank = assertThatBankActuallyExists(word.bank)

                word.compareWithDefaultCreateWordData()
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Anonymous user cannot create a word`() {
                val request = wordRequestFactory.createWordRequest()

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }
            }

            @Test
            fun `400 - Word cannot be created without example sentences`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    exampleSentences = emptySet()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be created with more than 5 example sentences`() {
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

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be created with an example sentence that has more than 255 characters`() {
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

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be created with bankId and bankToCreate at the same time`() {
                val authenticatedUser = mockAuthenticatedUser()

                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    bankId = bank.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be created with bankToCreate name matching already existing bank name`() {
                val authenticatedUser = mockAuthenticatedUser()

                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = bank.name
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `404 - Word cannot be created with bankId referring to a bank of another user`() {
                val authenticatedUser = mockAuthenticatedUser()
                val anotherUser = userSeeder.seedOneEntity()

                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    bankId = bankOfAnotherUser.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `400 - Word cannot be created with no use cases`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    useCases = emptySet()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `404 - Word cannot be created with an example sentence that has empty translation`() {
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

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be created with use case of length greater than 255 characters`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    useCases = mutableSetOf(
                        "a".repeat(256)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be created with more than 5 use cases`() {
                val authenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.createWordRequest(
                    authenticatedUser = authenticatedUser,
                    useCases = mutableSetOf<String>().apply {
                        repeat(6) { index ->
                            add("use case - $index")
                        }
                    }
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }
        }


    }

    @Nested
    @DisplayName("[PATCH] /api/v1/words/{id} - update a word")
    inner class UpdateWordTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - Word can be updated`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                    it.response
                }

                val updatedWord: Word = assertThatWordActuallyExists(response, authenticatedUser)

                updatedWord.compareWithDefaultUpdateWordData(
                    idOfWordToUpdate = word.id
                )
            }

            @Test
            fun `200 - Word can be updated with bankToCreate name identical to another bank name but for different user`() {
                val authenticatedUser = mockAuthenticatedUser()
                val anotherUser = userSeeder.seedOneEntity()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = bankOfAnotherUser.name
                    )
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                    it.response
                }

                val updatedWord: Word = assertThatWordActuallyExists(response, authenticatedUser)
                val bank: Bank = assertThatBankActuallyExists(updatedWord.bank)

                updatedWord.compareWithDefaultUpdateWordData(
                    idOfWordToUpdate = word.id
                )
            }

            @Test
            fun `200 - Word can be updated with only one field being specified`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequestWithNulls(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    origin = "new origin",
                )

                val response = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                    it.response
                }

                val updatedWord: Word = assertThatWordActuallyExists(response, authenticatedUser)

                updatedWord.detectChanges(
                    before = word,
                    changes = WordDataChanges(
                        origin = Optional("new origin")
                    )
                )
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Anonymous user cannot update a word`() {
                val word = wordSeeder.seedOneEntity()

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }
            }

            @Test
            fun `400 - Word cannot be updated with more than 5 example sentences`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
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

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be updated with an example sentence that has more than 255 characters`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    exampleSentences = mutableSetOf(
                        ExampleSentence(
                            sentence = "a".repeat(256),
                            translation = "przykladowe zdanie"
                        )
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be updated with an example sentence that has empty translation`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    exampleSentences = mutableSetOf(
                        ExampleSentence(
                            sentence = "example sentence",
                            translation = ""
                        )
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be updated with use case of length greater than 255 characters`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    useCases = mutableSetOf(
                        "a".repeat(256)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be updated with more than 5 use cases`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    useCases = mutableSetOf<String>().apply {
                        repeat(6) { index ->
                            add("use case - $index")
                        }
                    }
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be updated with bankId and bankToCreate at the same time`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    bankId = bank.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word cannot be updated with bankToCreate name matching already existing bank name`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = bank.name
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `404 - Word cannot be updated with bankId referring to a bank of another user`() {
                val authenticatedUser = mockAuthenticatedUser()
                val anotherUser = userSeeder.seedOneEntity()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    bankId = bankOfAnotherUser.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

            }


            @Test
            fun `404 - Word cannot be updated with bankId referring to a bank that does not exist`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    bankId = UUID.randomUUID()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `400 - Word cannot be updated with bankToCreate name that is empty`() {
                val authenticatedUser = mockAuthenticatedUser()

                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = ""
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `404 - Word cannot be updated by other user than the one who created it`() {
                val authenticatedUser = mockAuthenticatedUser()
                val anotherUser = userSeeder.seedOneEntity()

                val word = wordSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.updateWordRequest(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }
        }
    }

    @Nested
    @DisplayName("[DELETE] /api/v1/words/{id} - delete a word")
    inner class DeleteWordTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - Word can be deleted`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                wordService.findById(id = word.id, userId = authenticatedUser.userInfo.id) shouldNotBe null

                val request = wordRequestFactory.deleteWordRequestWithNulls(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findById(id = word.id, userId = authenticatedUser.userInfo.id) shouldBe null
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Word cannot be deleted by an anonymous user`() {
                val user: MockedAuthenticatedUser = mockAuthenticatedUser()
                val word = wordSeeder.seedOneEntityForUser(user.userInfo)

                wordService.findById(id = word.id, userId = user.userInfo.id) shouldNotBe null

                val request = wordRequestFactory.deleteWordRequestWithNulls(
                    wordId = word.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }


                wordService.findById(id = word.id, userId = user.userInfo.id) shouldNotBe null
            }

            @Test
            fun `404 - Word can be deleted only by its owner`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val anotherUser: User = userSeeder.seedOneEntity()

                val word = wordSeeder.seedOneEntityForUser(anotherUser)

                wordService.findById(id = word.id, userId = anotherUser.id) shouldNotBe null

                val request = wordRequestFactory.deleteWordRequestWithNulls(
                    wordId = word.id,
                    authenticatedUser = authenticatedUser
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

                wordService.findById(id = word.id, userId = anotherUser.id) shouldNotBe null
            }

            @Test
            fun `404 - Cannot remove non existing word`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.deleteWordRequestWithNulls(
                    wordId = UUID.randomUUID(),
                    authenticatedUser = authenticatedUser
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/{id}/change-bank - change word's bank")
    inner class ChangeSingleWordBankTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - Word's bank can be changed`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val firstBank: Bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val secondBank: Bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val word: Word = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(firstBank, true)
                )

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankId = secondBank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = word.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldBe secondBank.id
                }
            }

            @Test
            fun `200 - Word's bank can be changed to newly created bank`() {
                val newBankName = "NEW_EXTRA_BANK_NAME"

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val initialBank: Bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val word: Word = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                initialBank.name shouldNotBe newBankName

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankToCreate = CreateBankRequestData(
                        name = newBankName,
                        description = "x".repeat(64)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = word.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldNotBe initialBank.id
                    it.bank!!.name shouldBe newBankName
                }
            }

            @Test
            fun `200 - Word's bank can be change from null to already existing bank`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val word: Word = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )
                val newBank: Bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                wordService.findByIdOrFail(
                    id = word.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldBe null
                }

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankId = newBank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = word.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldBe newBank.id
                }
            }

            @Test
            fun `200 - Word's bank can be change from null to newly created bank`() {
                val newBankName = "NEW_EXTRA_BANK_NAME"

                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val word: Word = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankToCreate = CreateBankRequestData(
                        name = newBankName,
                        description = "x".repeat(64)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = word.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                    it.bank!!.name shouldBe newBankName
                }
            }

            @Test
            fun `200 - Word's bank can be unassigned`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val initialBank: Bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val word: Word = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankToCreate = null,
                    bankId = null
                )

                wordService.findByIdOrFail(
                    id = word.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                }

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = word.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldBe null
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Anonymous user cannot change word's bank`() {
                val word: Word = wordSeeder.seedOneEntity()

                val request = wordRequestFactory.changeBankForSingleWord(
                    wordId = word.id,
                    authenticatedUser = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }
            }

            @Test
            fun `404 - Word's bank cannot be changed by other user than the one who created it`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val anotherUser: User = userSeeder.seedOneEntity()

                val word: Word = wordSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `404 - Word's bank cannot be changed if word does not exist`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = UUID.randomUUID()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `404 - Word's bank cannot be changed if bank does not exist`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val word: Word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankId = UUID.randomUUID()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `404 - Word's bank cannot be changed if bank does not belong to the user`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val anotherUser: User = userSeeder.seedOneEntity()

                val word: Word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bank: Bank = bankSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankId = bank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `400 - Word's bank cannot be changed if both bankId and bankToCreate are specified`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val word: Word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bank: Bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankId = bank.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word's bank cannot be changed if bankToCreate name is empty`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val word: Word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = ""
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word's bank cannot be changed if bankToCreate name is identical to already existing bank name`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val word: Word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bank: Bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = bank.name
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word's bank cannot be changed if bankToCreate description is longer than 255 characters`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val word: Word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        description = "x".repeat(256)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST
                }
            }

            @Test
            fun `400 - Word's bank cannot be changed if bankToCreate description is empty`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()

                val word: Word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        description = ""
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST
                }
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/change-bank-for-multiple-words - change bank for multiple words")
    inner class ChangeBankForManyWordsAtTheSameTime {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - Words' bank can be changed from null to an existing bank`() {
                val authenticatedUser: MockedAuthenticatedUser = mockAuthenticatedUser()
                val bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    amount = 5,
                    bank = Optional(null, true)
                )

                words.forEach {
                    it.bank shouldBe null
                }

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankId = bank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                words.forEach {
                    wordService.findByIdOrFail(
                        id = it.id,
                        userId = authenticatedUser.userInfo.id
                    ).let {
                        bank shouldNotBe null
                        bank.id shouldBe bank.id
                    }
                }
            }
        }
    }


    private fun assertThatWordActuallyExists(
        response: MockHttpServletResponse,
        authenticatedUser: MockedAuthenticatedUser
    ): Word {
        val responseBody: WordDTO = getResponseBody<WordDTO>(response)
        assertNotNull(responseBody.id)

        assertEquals(authenticatedUser.userInfo.id, responseBody.user.id)

        val valueSavedInDatabase: Word? = wordRepository.findByIdOrNull(responseBody.id)

        assertNotNull(valueSavedInDatabase)

        valueSavedInDatabase!!.compareWith(
            wordMapper.toEntity(responseBody)
        )

        return valueSavedInDatabase
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
