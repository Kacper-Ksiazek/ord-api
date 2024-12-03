package com.backend.ord.controllers

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.responses.PaginatedDataResponse
import com.backend.ord.api.responses.words.WordAsGetManyWordResponse
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
import com.backend.ord.enums.Language.LanguageName
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
import org.junit.jupiter.api.BeforeEach
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
import com.backend.ord.enums.Word.WordExtraMark
import com.backend.ord.enums.Word.WordType
import com.backend.ord.api.requests.enums.SortDirection
import com.backend.ord.api.requests.word.enums.GetAllWordsSortOptions
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.seeders.entities.BankGroupSeeder
import com.backend.ord.seeders.factories.WordMockFactory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


import java.util.*

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@DisplayName("- WordsController")
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
    private val wordMapper: WordMapper,
    private val bankGroupSeeder: BankGroupSeeder
) : ControllerTestBase(mockMvc!!, objectMapper, jwtProperties) {
    @Autowired
    private lateinit var userMapper: UserMapper

    @Autowired
    private lateinit var wordMockFactory: WordMockFactory
    private val BASE_URL = "/api/v1/words/"

    private val wordRequestFactory = WordRequestFactory(
        BASE_URL = BASE_URL,
        objectMapper = objectMapper
    )

    lateinit var authenticatedUser: MockedAuthenticatedUser;

    @BeforeEach
    fun beforeEach() {
        authenticatedUser = mockAuthenticatedUser()
    }

    @Nested
    @DisplayName("[GET] /api/v1/words/ - get many words")
    inner class GetManyWords {
        val learningLanguage: LanguageName = LanguageName.NORWEGIAN;

        @BeforeEach
        fun seedDatabaseWithWords() {
            wordSeeder.seedMultipleEntitiesForUser(
                user = authenticatedUser.userInfo,
                amount = 100,
                language = learningLanguage
            )
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            private fun makeManyWordsRequest(
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
            ): PaginatedDataResponse<WordAsGetManyWordResponse> {
                val request = wordRequestFactory.getManyWordsRequest(
                    authenticatedUser = authenticatedUser,
                    language = learningLanguage,

                    page = page,
                    perPage = perPage,

                    wordType = wordType,
                    searchingPhrase = searchingPhrase,
                    bookmarkedOnly = bookmarkedOnly,
                    wordExtraMark = wordExtraMark,

                    banksIds = banksIds,
                    bankGroupsIds = banksGroupsIds,

                    sortDirection = sortDirection,
                    sortBy = sortBy
                )

                val response: MockHttpServletResponse = mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                    it.response
                }

                return getResponseBody<PaginatedDataResponse<WordAsGetManyWordResponse>>(response)
            }


            @Test
            fun `200 - Words can be fetched`() {
                makeManyWordsRequest()
            }

            @Test
            fun `200 - All words should belong to the user who requested them`() {
                // Generate a few random words for different user
                val anotherUser = userSeeder.seedOneEntity()

                wordSeeder.seedMultipleEntitiesForUser(
                    user = anotherUser,
                    amount = 10,
                    language = learningLanguage
                )

                val expectedAmountOfAllWords = wordRepository.findAllForUser(authenticatedUser.userInfo.id).size
                val actualAmountOfWords = makeManyWordsRequest(perPage = 500).data.size

                actualAmountOfWords shouldBe expectedAmountOfAllWords
            }

            @Test
            fun `200 - Words can be fetched with pagination`() {
                val pageOne = makeManyWordsRequest(page = 0, perPage = 10);
                val pageSix = makeManyWordsRequest(page = 5, perPage = 10);

                pageOne.pagination.totalPages shouldBe 10

                pageOne.data.size shouldBe 10
                pageOne.pagination.page shouldBe 0

                pageSix.data.size shouldBe 10
                pageSix.pagination.page shouldBe 5

                pageOne.data.forEach { wordFromPageOne ->
                    pageSix.data.find { it.id == wordFromPageOne.id } shouldBe null
                }
            }

            @Test
            fun `200 - Words can be fetched with sorting`() {
                val withDefaultSorting = makeManyWordsRequest();
                val ascSorted = makeManyWordsRequest(sortBy = GetAllWordsSortOptions.ORIGIN);
                val descSorted =
                    makeManyWordsRequest(sortBy = GetAllWordsSortOptions.ORIGIN, sortDirection = SortDirection.DESC);

                repeat(withDefaultSorting.data.size) { index ->
                    withDefaultSorting.data[index].origin shouldNotBe ascSorted.data[index].origin
                    withDefaultSorting.data[index].origin shouldNotBe descSorted.data[index].origin
                }
            }

            @Test
            fun `200 - Words can be fetched with filtering - by word type`() {
                val body = makeManyWordsRequest(
                    wordType = WordType.IDIOM,
                    perPage = 500
                );

                body.data.forEach {
                    it.type shouldBe WordType.IDIOM
                }
            }

            @Test
            fun `200 - Words can be fetched with filtering - by searching phrase`() {
                val user = userMapper.toEntity(authenticatedUser.userInfo)
                val expectedWordMark: String = "EXPECTED_WORD_MARK"

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        user = user,
                        origin = "kacper1",
                        translation = expectedWordMark,
                        translatedFrom = learningLanguage
                    )
                )

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        user = user,
                        origin = "KACPER2",
                        translation = expectedWordMark,
                        translatedFrom = learningLanguage
                    )
                )

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        user = user,
                        origin = "per3",
                        translation = expectedWordMark,
                        translatedFrom = learningLanguage
                    )
                )

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        user = user,
                        origin = expectedWordMark + "1",
                        translation = "kacper",
                        translatedFrom = learningLanguage
                    )
                )

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        user = user,
                        origin = expectedWordMark + "2",
                        translation = "KACPER",
                        translatedFrom = learningLanguage
                    )
                )

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        user = user,
                        origin = expectedWordMark + "3",
                        translation = "PER",
                        translatedFrom = learningLanguage
                    )
                )

                val body = makeManyWordsRequest(
                    searchingPhrase = "kacper",
                    perPage = 500
                );

                body.data.forEach { t ->
                    assert(t.origin.contains(expectedWordMark) || t.translation.contains(expectedWordMark))
                }
            }

            @Test
            fun `200 - Words can be fetched with filtering - by extra mark`() {
                val body = makeManyWordsRequest(
                    wordExtraMark = WordExtraMark.OFFENSIVE,
                    perPage = 500
                );

                body.data.forEach {
                    it.extraMark shouldBe WordExtraMark.OFFENSIVE
                }
            }

            @Test
            fun `200 - Words can be fetched with filtering - by bookmarked`() {
                val body = makeManyWordsRequest(
                    bookmarkedOnly = true,
                    perPage = 500
                );

                body.data.forEach {
                    it.isBookmarked shouldBe true
                }
            }

            @Test
            fun `200 - Words can be fetched with filtering - by bank`() {
                val bankOne = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bankTwo = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                wordSeeder.seedMultipleEntitiesForUser(
                    amount = 10,
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    bank = Optional(bankOne),
                    language = learningLanguage
                )

                wordSeeder.seedMultipleEntitiesForUser(
                    amount = 10,
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    bank = Optional(bankTwo),
                    language = learningLanguage
                )

                val body = makeManyWordsRequest(
                    banksIds = setOf(bankOne.id),
                    perPage = 500
                );

                body.data.forEach {
                    it.bank?.id shouldBe bankOne.id
                }
            }

            @Test
            fun `200 - Words can be fetched with filtering - by bank group`() {
                val bankGroupOne = bankGroupSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bankGroupTwo = bankGroupSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val bankOne = bankSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bankGroup = bankGroupOne
                )

                val bankTwo = bankSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bankGroup = bankGroupTwo
                )

                wordSeeder.seedMultipleEntitiesForUser(
                    amount = 24,
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    bank = Optional(bankOne),
                    language = learningLanguage
                )

                wordSeeder.seedMultipleEntitiesForUser(
                    amount = 5,
                    user = userMapper.toEntity(authenticatedUser.userInfo),
                    bank = Optional(bankTwo),
                    language = learningLanguage
                )

                val body = makeManyWordsRequest(
                    banksGroupsIds = setOf(bankGroupOne.id),
                    perPage = 500
                );

                println(body.data.map { it.bank?.bankGroup?.id })

                body.data.forEach {
                    it.bank?.bankGroup?.id shouldBe bankGroupOne.id
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            private fun makeManyWordsRequestUnsafe(
                page: Any? = null,
                perPage: Any? = null,

                language: Any? = learningLanguage,
                wordType: Any? = null,
                searchingPhrase: Any? = null,
                bookmarkedOnly: Any? = null,
                wordExtraMark: Any? = null,

                banksIds: Any? = null,
                bankGroupsIds: Any? = null,

                sortDirection: Any? = null,
                sortBy: Any? = null,
            ) {
                val request = wordRequestFactory.getManyWordsRequestUnsafe(
                    authenticatedUser = authenticatedUser,
                    language = language,

                    page = page,
                    perPage = perPage,

                    wordType = wordType,
                    searchingPhrase = searchingPhrase,
                    bookmarkedOnly = bookmarkedOnly,
                    wordExtraMark = wordExtraMark,

                    banksIds = banksIds,
                    bankGroupsIds = bankGroupsIds,

                    sortDirection = sortDirection,
                    sortBy = sortBy
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                    it.response
                }
            }


            @Test
            fun `403 - Anonymous user cannot fetch words`() {
                val request = wordRequestFactory.getManyWordsRequest(
                    authenticatedUser = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }
            }

            @Test
            fun `400 - Words cannot be fetched without a language specified`() {
                makeManyWordsRequestUnsafe(language = null)
            }

            @ParameterizedTest
            @ValueSource(strings = ["-1", "0.5", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - page`(parameter: String) {
                makeManyWordsRequestUnsafe(
                    page = parameter
                )
            }

            @ParameterizedTest
            @ValueSource(strings = ["-1", "0.5", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - perPage`(parameter: String) {
                makeManyWordsRequestUnsafe(
                    perPage = parameter
                )
            }

            @ParameterizedTest
            @ValueSource(strings = ["-1", "0.5", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - sortBy`(parameter: String) {
                makeManyWordsRequestUnsafe(
                    sortBy = parameter
                )
            }

            @ParameterizedTest
            @ValueSource(strings = ["desc", "asc", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - sortDirection`(parameter: String) {
                makeManyWordsRequestUnsafe(
                    sortDirection = parameter
                )
            }

            @ParameterizedTest
            @ValueSource(strings = ["-1", "0.5", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - wordType`() {
                makeManyWordsRequestUnsafe(
                    wordType = "abc"
                )
            }

            @Test
            fun `400 - Words cannot be fetched with invalid param - searchPhrase too long`() {
                makeManyWordsRequestUnsafe(
                    searchingPhrase = "x".repeat(65)
                )
            }

            @Test
            fun `400 - Words cannot be fetched with invalid param - searchPhrase too short`() {
                makeManyWordsRequestUnsafe(
                    searchingPhrase = "x".repeat(1)
                )
            }

            @ParameterizedTest
            @ValueSource(strings = ["-1", "0.5", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - extraMark`(parameter: String) {
                makeManyWordsRequestUnsafe(
                    wordExtraMark = parameter
                )
            }

            @ParameterizedTest
            @ValueSource(strings = ["-1", "0.5", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - bookmarkedOnly`(parameter: String) {
                makeManyWordsRequestUnsafe(
                    bookmarkedOnly = parameter
                )
            }

            @ParameterizedTest
            @ValueSource(strings = ["-1", "0.5", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - banksIds - non list `(parameter: String) {
                makeManyWordsRequestUnsafe(
                    banksIds = parameter
                )
            }

            @Test
            fun `400 - Words cannot be fetched with invalid param - banksIds - list of non uuids`() {
                makeManyWordsRequestUnsafe(
                    banksIds = listOf("abc", "def")
                )
            }

            @Test
            fun `400 - Words cannot be fetched with invalid param - banksIds - list uuids with duplicates`() {
                val generatedUUID = UUID.randomUUID()

                makeManyWordsRequestUnsafe(
                    banksIds = listOf(generatedUUID, generatedUUID)
                )
            }

            @ParameterizedTest
            @ValueSource(strings = ["-1", "0.5", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - bankGroupsIds - non list`(parameter: String) {
                makeManyWordsRequestUnsafe(
                    bankGroupsIds = parameter
                )
            }

            @Test
            fun `400 - Words cannot be fetched with invalid param - bankGroupsIds - list of non uuids`() {
                makeManyWordsRequestUnsafe(
                    bankGroupsIds = listOf("abc", "def")
                )
            }

            @Test
            fun `400 - Words cannot be fetched with invalid param - bankGroupsIds - list uuids with duplicates`() {
                val generatedUUID = UUID.randomUUID()

                makeManyWordsRequestUnsafe(
                    bankGroupsIds = listOf(generatedUUID, generatedUUID)
                )
            }

        }
    }

    @Nested
    @DisplayName("[GET] /api/v1/words/{id} - get a single word")
    inner class GetSingleWords {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - Word can be fetched by its owner`() {
                // TODO
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Anonymous user cannot fetch a word`() {
                // TODO
            }

            @Test
            fun `404 - Word cannot be fetched by other user than the one who created it`() {
                // TODO
            }

            @Test
            fun `404 - Word cannot be fetched if it does not exist`() {
                // TODO
            }

            @Test
            fun `400 - Word cannot be fetched if id is not a valid UUID`() {
                // TODO
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/ - create a word")
    inner class CreateWordTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - Word can be created without bank being specified`() {
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
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                wordService.findById(id = word.id, userId = authenticatedUser.userInfo.id) shouldNotBe null

                val request = wordRequestFactory.deleteWordRequestWithNulls(
                    wordId = word.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }


                wordService.findById(id = word.id, userId =authenticatedUser.userInfo.id) shouldNotBe null
            }

            @Test
            fun `404 - Word can be deleted only by its owner`() {
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
                val word: Word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        description = "x".repeat(256)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word's bank cannot be changed if bankToCreate description is empty`() {
                val word: Word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = word.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        description = ""
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
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
                val bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
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

            @Test
            fun `200 - Words' bank can be changed from null to a newly created bank`() {
                val bankName = "NEW_EXTRA_BANK_NAME"

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                words.forEach {
                    it.bank shouldBe null
                }

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankToCreate = CreateBankRequestData(
                        name = bankName,
                        description = "x".repeat(64)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                words.forEach {
                    wordService.findByIdOrFail(
                        id = it.id,
                        userId = authenticatedUser.userInfo.id
                    ).let { word ->
                        word.bank shouldNotBe null
                        word.bank!!.name shouldBe bankName
                    }
                }
            }

            @Test
            fun `200 - Words' bank can be changed from an existing bank to another existing bank`() {
                val firstBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val secondBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(firstBank, true)
                )

                words.forEach {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldBe firstBank.id
                }

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankId = secondBank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                words.forEach {
                    wordService.findByIdOrFail(
                        id = it.id,
                        userId = authenticatedUser.userInfo.id
                    ).let { word ->
                        word.bank shouldNotBe null
                        word.bank!!.id shouldBe secondBank.id
                    }
                }
            }

            @Test
            fun `200 - Words' bank can be changed from an existing bank to a newly created bank`() {
                val newBankName = "NEW_EXTRA_BANK_NAME"

                val firstBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(firstBank, true)
                )

                words.forEach {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldBe firstBank.id
                }

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankToCreate = CreateBankRequestData(
                        name = newBankName,
                        description = "x".repeat(64)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                words.forEach {
                    wordService.findByIdOrFail(
                        id = it.id,
                        userId = authenticatedUser.userInfo.id
                    ).let { word ->
                        word.bank shouldNotBe null
                        word.bank!!.name shouldBe newBankName
                    }
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Anonymous user cannot change words' bank`() {
                val user: User = userSeeder.seedOneEntity()

                val initialBank = bankSeeder.seedOneEntityForUser(user = user)
                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = user,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    wordIds = words,
                    authenticatedUser = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed by other user than the one who created them`() {
                val anotherUser: User = userSeeder.seedOneEntity()

                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val bankToChange = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = anotherUser,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankId = bankToChange.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed if one of the words does not exist`() {
                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val bankToChange = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true),
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words.map { it.id } + UUID.randomUUID(),
                    bankId = bankToChange.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed if bank does not exist`() {
                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankId = UUID.randomUUID()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed if bank does not belong to the user`() {
                val anotherUser: User = userSeeder.seedOneEntity()

                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val bankOfAnotherUser: Bank = bankSeeder.seedOneEntityForUser(anotherUser)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankId = bankOfAnotherUser.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if both bankId and bankToCreate are specified`() {
                val bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankId = bank.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if neither bankId nor bankToCreate are specified`() {
                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankId = null,
                    bankToCreate = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate name is empty`() {
                val bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = ""
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate name is identical to already existing bank name`() {
                val bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = bank.name
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate description is longer than 255 characters`() {
                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        description = "x".repeat(256)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate description is empty`() {
                val bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val words: List<Word> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = words,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        description = ""
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                words.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
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
