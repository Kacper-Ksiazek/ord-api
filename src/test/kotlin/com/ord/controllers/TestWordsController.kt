package com.ord.controllers

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserMapper
import com.ord.core.word.api.requests.dto.UnsafeGetManyWordsRequest
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.requests.enums.WordToggleableProperty
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordDTO
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.WordMapper
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.repository.WordRepository
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank.service.BankService
import com.ord.seeders.entities.BankGroupSeeder
import com.ord.seeders.entities.BankSeeder
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.entities.WordSeeder
import com.ord.seeders.factories.WordFactory
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import com.ord.shared.domain.enums.SortDirection
import com.ord.testing_utils.api.clients.WordsAPIClient
import com.ord.testing_utils.api.dto.APIClientResponse
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("- WordsController")
class TestWordsController @Autowired constructor(
    private val wordRepository: WordRepository,
    private val bankSeeder: BankSeeder,
    private val bankService: BankService,
    private val userSeeder: UserSeeder,
    private val wordSeeder: WordSeeder,
    private val wordMapper: WordMapper,
    private val bankGroupSeeder: BankGroupSeeder,
    private var wordMockFactory: WordFactory,
    private val userMapper: UserMapper,

    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient

) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
) {
    private val wordsAPIClient = WordsAPIClient(webClient)

    lateinit var authenticatedUser: MockedAuthenticatedUser

    @BeforeEach
    fun beforeEach() {
        authenticatedUser = mockAuthenticatedUser()
    }

    @Nested
    @DisplayName("[GET] /api/v1/words/ - get many words")
    inner class GetManyWords {
        val learningLanguage: LanguageName = LanguageName.NORWEGIAN

        @BeforeEach
        fun seedDatabaseWithWords() {
            wordSeeder.seedMultipleEntitiesForUser(
                userId = authenticatedUser.userInfo.id,
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
                completed: Boolean? = null,
                searchingPhrase: String? = null,
                bookmarked: Boolean? = null,
                wordExtraMark: WordExtraMark? = null,

                banksIds: Set<UUID>? = null,
                banksGroupsIds: Set<UUID>? = null,

                sortDirection: SortDirection? = null,
                sortBy: GetAllWordsSortOptions? = null,
            ): PaginatedDataResponse<WordListItem> {
                val request = UnsafeGetManyWordsRequest(
                    language = learningLanguage,
                    page = page,
                    perPage = perPage,
                    wordType = wordType,
                    completed = completed,
                    searchingPhrase = searchingPhrase,
                    bookmarked = bookmarked,
                    wordExtraMark = wordExtraMark,
                    banksIds = banksIds?.toList(),
                    bankGroupsIds = banksGroupsIds?.toList(),
                    sortDirection = sortDirection,
                    sortBy = sortBy
                )

                val response = wordsAPIClient.getManyWords(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                return response.body!!
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
                    userId = anotherUser.id!!,
                    amount = 10,
                    language = learningLanguage
                )

                val expectedAmountOfAllWords =
                    wordRepository.findAllByUserId(authenticatedUser.userInfo.id).collectList().block()!!.size
                val actualAmountOfWords = makeManyWordsRequest(perPage = 500).data.size

                actualAmountOfWords shouldBe expectedAmountOfAllWords
            }

            @Test
            fun `200 - Words can be fetched with pagination`() {
                val pageOne = makeManyWordsRequest(page = 0, perPage = 10)
                val pageSix = makeManyWordsRequest(page = 5, perPage = 10)

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
            fun `200 - Words can be fetched with sorting - DESC`() {
                val descSorted =
                    makeManyWordsRequest(sortBy = GetAllWordsSortOptions.ORIGIN, sortDirection = SortDirection.DESC)

                descSorted.data.map { it.origin }.zipWithNext { previous, current ->
                    // Check if the previous value is greater than the current one
                    current.compareTo(previous) shouldBeLessThan 0
                }
            }

            @Test
            fun `200 - Words can be fetched with sorting - ASC`() {
                val descSorted =
                    makeManyWordsRequest(sortBy = GetAllWordsSortOptions.ORIGIN, sortDirection = SortDirection.ASC)

                descSorted.data.map { it.origin }.zipWithNext { previous, current ->
                    // Check if the previous value is greater than the current one
                    previous.compareTo(current) shouldBeLessThan 0
                }
            }

            @Test
            fun `200 - Words can be fetched with filtering - by word type`() {
                val body = makeManyWordsRequest(
                    wordType = WordType.IDIOM,
                    perPage = 500
                )

                body.data.forEach {
                    it.type shouldBe WordType.IDIOM
                }
            }

            @Test
            fun `200 - Words can be fetched with filtering - by searching phrase`() {
                val expectedWordMark = "EXPECTED_WORD_MARK"

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        userId = authenticatedUser.userInfo.id,
                        origin = "kacper1",
                        translation = expectedWordMark,
                        translatedFrom = learningLanguage
                    )
                ).block()

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        userId = authenticatedUser.userInfo.id,
                        origin = "KACPER2",
                        translation = expectedWordMark,
                        translatedFrom = learningLanguage
                    )
                ).block()

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        userId = authenticatedUser.userInfo.id,
                        origin = "per3",
                        translation = expectedWordMark,
                        translatedFrom = learningLanguage
                    )
                ).block()

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        userId = authenticatedUser.userInfo.id,
                        origin = expectedWordMark + "1",
                        translation = "kacper",
                        translatedFrom = learningLanguage
                    )
                ).block()

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        userId = authenticatedUser.userInfo.id,
                        origin = expectedWordMark + "2",
                        translation = "KACPER",
                        translatedFrom = learningLanguage
                    )
                ).block()

                wordRepository.save(
                    wordMockFactory.mockEntity(
                        userId = authenticatedUser.userInfo.id,
                        origin = expectedWordMark + "3",
                        translation = "PER",
                        translatedFrom = learningLanguage
                    )
                ).block()

                val body = makeManyWordsRequest(
                    searchingPhrase = "kacper",
                    perPage = 500
                )

                body.data.forEach { t ->
                    assert(t.origin.contains(expectedWordMark) || t.translation.contains(expectedWordMark))
                }
            }

            @Test
            fun `200 - Words can be fetched with filtering - by extra mark`() {
                val body = makeManyWordsRequest(
                    wordExtraMark = WordExtraMark.OFFENSIVE,
                    perPage = 500
                )

                body.data.forEach {
                    it.extraMark shouldBe WordExtraMark.OFFENSIVE
                }
            }

            @ParameterizedTest
            @ValueSource(booleans = [true, false])
            fun `200 - Words can be fetched with filtering - by bookmarked `(bookmarked: Boolean) {
                val wordsToAdd = List(10) {
                    wordMockFactory
                        .mockEntity(
                            userId = authenticatedUser.userInfo.id,
                        ).apply { isBookmarked = bookmarked }
                }

                wordRepository.saveAll(wordsToAdd).collectList().block()

                val body = makeManyWordsRequest(
                    bookmarked = bookmarked,
                    perPage = 500
                )

                body.data.forEach {
                    it.isBookmarked shouldBe bookmarked
                }
            }

            @ParameterizedTest
            @ValueSource(booleans = [true, false])
            fun `200 - Words can be fetched with filtering - by completed status `(completed: Boolean) {
                val wordsToAdd = List(10) {
                    wordMockFactory.mockEntity(
                        userId = authenticatedUser.userInfo.id,
                    ).apply { isCompleted = completed }
                }

                wordRepository.saveAll(wordsToAdd).collectList().block()

                val body: PaginatedDataResponse<WordListItem> = makeManyWordsRequest(
                    completed = completed,
                    perPage = 500
                )

                body.data.forEach {
                    it.isCompleted shouldBe completed
                }
            }


            @Test
            fun `200 - Words can be fetched with filtering - by bank`() {
                val bankOne = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bankTwo = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                wordSeeder.seedMultipleEntitiesForUser(
                    amount = 10,
                    userId = authenticatedUser.userInfo.id,
                    bankId = bankOne.id,
                    language = learningLanguage
                )

                wordSeeder.seedMultipleEntitiesForUser(
                    amount = 10,
                    userId = authenticatedUser.userInfo.id,
                    bankId = bankTwo.id,
                    language = learningLanguage
                )

                val body = makeManyWordsRequest(
                    banksIds = setOf(bankOne.id!!),
                    perPage = 500
                )

                body.data.forEach {
                    it.bank?.name shouldBe bankOne.name
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
                    userId = authenticatedUser.userInfo.id,
                    bankId = bankOne.id,
                    language = learningLanguage
                )

                wordSeeder.seedMultipleEntitiesForUser(
                    amount = 5,
                    userId = authenticatedUser.userInfo.id,
                    bankId = bankTwo.id,
                    language = learningLanguage
                )

                val body = makeManyWordsRequest(
                    banksGroupsIds = setOf(bankGroupOne.id!!),
                    perPage = 500
                )

                body.data.forEach {
                    it.bank?.bankGroup?.name shouldBe bankGroupOne.name
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
                bookmarked: Any? = null,
                completed: Any? = null,
                wordExtraMark: Any? = null,

                banksIds: Any? = null,
                bankGroupsIds: Any? = null,

                sortDirection: Any? = null,
                sortBy: Any? = null,
                user: MockedAuthenticatedUser? = authenticatedUser,
                expectedStatus: HttpStatus = HttpStatus.BAD_REQUEST
            ): APIClientResponse<PaginatedDataResponse<WordListItem>?> {
                val request = UnsafeGetManyWordsRequest(
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
                    sortDirection = sortDirection,
                    sortBy = sortBy
                )

                val response = wordsAPIClient.getManyWords(
                    body = request,
                    user = user
                )

                response.status shouldBe expectedStatus
                return response
            }


            @Test
            fun `401 - Anonymous user cannot fetch words`() {
                makeManyWordsRequestUnsafe(user = null, expectedStatus = HttpStatus.UNAUTHORIZED)
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
                    searchingPhrase = "x".repeat(0)
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
            fun `400 - Words cannot be fetched with invalid param - bookmarked`(parameter: String) {
                makeManyWordsRequestUnsafe(
                    bookmarked = parameter
                )
            }

            @ParameterizedTest
            @ValueSource(strings = ["-1", "0.5", "1.5", "abc"])
            fun `400 - Words cannot be fetched with invalid param - completed`(parameter: String) {
                makeManyWordsRequestUnsafe(
                    completed = parameter
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
            fun `200 - Word without bank can be fetched by its owner`() {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = null
                )

                val response = wordsAPIClient.getWord(
                    id = wordEntity.id!!,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                val fetchedWord = response.body!!
                fetchedWord.id shouldBe wordEntity.id
            }

            @Test
            fun `200 - Word with bank but without bank group can be fetched by its owner`() {
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val wordWithBank = wordSeeder.seedOneEntityForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = bank.id
                )

                val response = wordsAPIClient.getWord(
                    id = wordWithBank.id!!,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                val fetchedWord = response.body!!
                fetchedWord.id shouldBe wordWithBank.id
                fetchedWord.bank shouldNotBe null
                fetchedWord.bank?.name shouldBe bank.name
                fetchedWord.bank?.bankGroup shouldBe null
            }

            @Test
            fun `200 - Word with bank and bank group can be fetched by its owner`() {
                val bankGroup = bankGroupSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bank = bankSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bankGroup = bankGroup
                )

                val wordWithBankAndGroup = wordSeeder.seedOneEntityForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = bank.id
                )

                val response = wordsAPIClient.getWord(
                    id = wordWithBankAndGroup.id!!,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null

                val fetchedWord = response.body!!
                fetchedWord.id shouldBe wordWithBankAndGroup.id
                fetchedWord.bank shouldNotBe null
                fetchedWord.bank?.name shouldBe bank.name
                fetchedWord.bank?.bankGroup shouldNotBe null
                fetchedWord.bank?.bankGroup?.name shouldBe bankGroup.name
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - Anonymous user cannot fetch a word`() {
                val word = wordSeeder.seedOneEntity()

                val response = wordsAPIClient.getWord(
                    id = word.id!!,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - Word cannot be fetched by other user than the one who created it`() {
                val anotherUser = userSeeder.seedOneEntity()
                val word = wordSeeder.seedOneEntityForUser(
                    userId = anotherUser.id!!,
                    bankId = null
                )

                val response = wordsAPIClient.getWord(
                    id = word.id!!,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - Word cannot be fetched if it does not exist`() {
                val response = wordsAPIClient.getWord(
                    id = UUID.randomUUID(),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @ParameterizedTest
            @ValueSource(strings = ["abc", "-1", "123123123"])
            fun `400 - Word cannot be fetched if id is not a valid UUID`(id: String) {
                val response = wordsAPIClient.get(
                    url = "/api/v1/words/$id",
                    user = authenticatedUser,
                    responseBodyType = object : ParameterizedTypeReference<SingleWordResponse>() {}
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    /* MIGRATION STEP 3
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

                val wordEntity: WordEntity = assertThatWordActuallyExists(response, authenticatedUser)

                wordEntity.compareWithDefaultCreateWordData()
                assertEquals(bank.id, wordEntity.bank?.id)
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

                val wordEntity: WordEntity = assertThatWordActuallyExists(response, authenticatedUser)

                assertThatBankActuallyExists(wordEntity.bank)

                wordEntity.compareWithDefaultCreateWordData()
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

                val wordEntity: WordEntity = assertThatWordActuallyExists(response, authenticatedUser)

                wordEntity.compareWithDefaultCreateWordData(
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

                val wordEntity: WordEntity = assertThatWordActuallyExists(response, authenticatedUser)

                assertThatBankActuallyExists(wordEntity.bank)

                wordEntity.compareWithDefaultCreateWordData()
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
     */

    /* MIGRATION STEP 4
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

                val updatedWordEntity: WordEntity = assertThatWordActuallyExists(response, authenticatedUser)

                updatedWordEntity.compareWithDefaultUpdateWordData(
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

                val updatedWordEntity: WordEntity = assertThatWordActuallyExists(response, authenticatedUser)
                assertThatBankActuallyExists(updatedWordEntity.bank)

                updatedWordEntity.compareWithDefaultUpdateWordData(
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

                val updatedWordEntity: WordEntity = assertThatWordActuallyExists(response, authenticatedUser)

                updatedWordEntity.detectChanges(
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
     */

    /* MIGRATION STEP 5
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


                wordService.findById(id = word.id, userId = authenticatedUser.userInfo.id) shouldNotBe null
            }

            @Test
            fun `404 - Word can be deleted only by its owner`() {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

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
     */

    /* MIGRATION STEP 6
    @Nested
    @DisplayName("[POST] /api/v1/words/{id}/change-bank - change word's bank")
    inner class ChangeSingleWordBankTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - Word's bank can be changed`() {
                val firstBank: BankEntity = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val secondBank: BankEntity = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(firstBank, true)
                )

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    bankId = secondBank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldBe secondBank.id
                }
            }

            @Test
            fun `200 - Word's bank can be changed to newly created bank`() {
                val newBankName = "NEW_EXTRA_BANK_NAME"

                val initialBank: BankEntity = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                initialBank.name shouldNotBe newBankName

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    bankToCreate = CreateBankRequest(
                        name = newBankName,
                        description = "x".repeat(64)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldNotBe initialBank.id
                    it.bank!!.name shouldBe newBankName
                }
            }

            @Test
            fun `200 - Word's bank can be change from null to already existing bank`() {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )
                val newBank: BankEntity = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                wordService.findByIdOrFail(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldBe null
                }

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    bankId = newBank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldBe newBank.id
                }
            }

            @Test
            fun `200 - Word's bank can be change from null to newly created bank`() {
                val newBankName = "NEW_EXTRA_BANK_NAME"

                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    bankToCreate = CreateBankRequest(
                        name = newBankName,
                        description = "x".repeat(64)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                    it.bank!!.name shouldBe newBankName
                }
            }

            @Test
            fun `200 - Word's bank can be unassigned`() {
                val initialBank: BankEntity = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    bankToCreate = null,
                    bankId = null
                )

                wordService.findByIdOrFail(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).let {
                    it.bank shouldNotBe null
                }

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = wordEntity.id,
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
                val wordEntity: WordEntity = wordSeeder.seedOneEntity()

                val request = wordRequestFactory.changeBankForSingleWord(
                    wordId = wordEntity.id,
                    authenticatedUser = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }
            }

            @Test
            fun `404 - Word's bank cannot be changed by other user than the one who created it`() {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id
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
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    bankId = UUID.randomUUID()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `404 - Word's bank cannot be changed if bank does not belong to the user`() {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bank: BankEntity = bankSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    bankId = bank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `400 - Word's bank cannot be changed if both bankId and bankToCreate are specified`() {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bank: BankEntity = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    bankId = bank.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - Word's bank cannot be changed if bankToCreate name is empty`() {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
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
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bank: BankEntity = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
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
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
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
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.changeBankForSingleWord(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
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
     */

    /* MIGRATION STEP 7
    @Nested
    @DisplayName("[POST] /api/v1/words/change-bank-for-multiple-words - change bank for multiple words")
    inner class ChangeBankForManyWordsAtTheSameTime {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - Words' bank can be changed from null to an existing bank`() {
                val bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                wordEntities.forEach {
                    it.bank shouldBe null
                }

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankId = bank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordEntities.forEach {
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

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                wordEntities.forEach {
                    it.bank shouldBe null
                }

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankToCreate = CreateBankRequest(
                        name = bankName,
                        description = "x".repeat(64)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordEntities.forEach {
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

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(firstBank, true)
                )

                wordEntities.forEach {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldBe firstBank.id
                }

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankId = secondBank.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordEntities.forEach {
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

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(firstBank, true)
                )

                wordEntities.forEach {
                    it.bank shouldNotBe null
                    it.bank!!.id shouldBe firstBank.id
                }

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankToCreate = CreateBankRequest(
                        name = newBankName,
                        description = "x".repeat(64)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordEntities.forEach {
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
                val user: UserEntity = userSeeder.seedOneEntity()

                val initialBank = bankSeeder.seedOneEntityForUser(user = user)
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = user,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    wordEntityIds = wordEntities,
                    authenticatedUser = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed by other user than the one who created them`() {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val bankToChange = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = anotherUser,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankId = bankToChange.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed if one of the words does not exist`() {
                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val bankToChange = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true),
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordIds = wordEntities.map { it.id } + UUID.randomUUID(),
                    bankId = bankToChange.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed if bank does not exist`() {
                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankId = UUID.randomUUID()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed if bank does not belong to the user`() {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)
                val bankOfAnotherUser: BankEntity = bankSeeder.seedOneEntityForUser(anotherUser)

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankId = bankOfAnotherUser.id
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if both bankId and bankToCreate are specified`() {
                val bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankId = bank.id,
                    bankToCreate = bankMockFactory.mockCreateRequestData()
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if neither bankId nor bankToCreate are specified`() {
                val initialBank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(initialBank, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankId = null,
                    bankToCreate = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank!!.id shouldBe initialBank.id
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate name is empty`() {
                bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = ""
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate name is identical to already existing bank name`() {
                val bank = bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        name = bank.name
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate description is longer than 255 characters`() {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        description = "x".repeat(256)
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate description is empty`() {
                bankSeeder.seedOneEntityForUser(user = authenticatedUser.userInfo)

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                    bank = Optional(null, true)
                )

                val request = wordRequestFactory.changeBankForMultipleWords(
                    authenticatedUser = authenticatedUser,
                    wordEntityIds = wordEntities,
                    bankToCreate = bankMockFactory.mockCreateRequestData(
                        description = ""
                    )
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id) }.forEach {
                    it.bank shouldBe null
                }
            }
        }
    }
     */

    /* MIGRATION STEP 8
    @Nested
    @DisplayName("[POST] /api/v1/words/{id}/toggle-property - toggle word's property")
    inner class TogglePropertyForOneWordTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - Word's boolean properties can be toggled from false to true`(property: WordToggleableProperty) {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                wordEntity.updateBooleanProperty(property, false)

                val request = wordRequestFactory.togglePropertyRequest(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    property = property
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).assertBooleanProperty(property, true)
            }

            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - Word's boolean properties can be toggled from true to false`(property: WordToggleableProperty) {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(
                    user = authenticatedUser.userInfo,
                )

                wordEntity.updateBooleanProperty(property, true)

                val request = wordRequestFactory.togglePropertyRequest(
                    authenticatedUser = authenticatedUser,
                    wordId = wordEntity.id,
                    property = property
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordService.findByIdOrFail(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).assertBooleanProperty(property, false)
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Anonymous user cannot toggle word's property`() {
                val wordEntity: WordEntity = wordSeeder.seedOneEntity()

                val request = wordRequestFactory.togglePropertyRequest(
                    wordId = wordEntity.id,
                    property = WordToggleableProperty.IS_COMPLETED,
                    authenticatedUser = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }
            }

            @Test
            fun `404 - Word's property cannot be toggled by other user than the one who created it`() {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(anotherUser)

                val request = wordRequestFactory.togglePropertyRequest(
                    wordId = wordEntity.id,
                    property = WordToggleableProperty.IS_COMPLETED,
                    authenticatedUser = authenticatedUser
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `404 - Word's property cannot be toggled if word does not exist`() {
                val request = wordRequestFactory.togglePropertyRequest(
                    wordId = UUID.randomUUID(),
                    property = WordToggleableProperty.IS_COMPLETED,
                    authenticatedUser = authenticatedUser
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `400 - Word's property cannot be toggled if property is not boolean`() {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = wordRequestFactory.togglePropertyRequest(
                    wordId = wordEntity.id,
                    property = null,
                    authenticatedUser = authenticatedUser
                )

                // Override the property param
                request.param("property", "not_boolean")

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }
        }
    }
     */

    /* MIGRATION STEP 9
    @Nested
    @DisplayName("[POST] /api/v1/words/toggle-property-for-multiple-words - toggle property for multiple words")
    inner class TogglePropertyForManyWordsTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - Words' boolean properties can be toggled from false to true`(property: WordToggleableProperty) {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                )

                wordEntities.forEach {
                    it.updateBooleanProperty(property, false)
                }

                val request = wordRequestFactory.togglePropertyForMultipleWordsRequest(
                    authenticatedUser = authenticatedUser,
                    wordEntities = wordEntities,
                    property = property
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id, authenticatedUser.userInfo.id) }.forEach {
                    it.assertBooleanProperty(property, true)
                }
            }

            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - Words' boolean properties can be toggled from true to false`(property: WordToggleableProperty) {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                )

                wordEntities.forEach {
                    it.updateBooleanProperty(property, true)
                }

                val request = wordRequestFactory.togglePropertyForMultipleWordsRequest(
                    authenticatedUser = authenticatedUser,
                    wordEntities = wordEntities,
                    property = property
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                wordEntities.map { wordService.findByIdOrFail(it.id, authenticatedUser.userInfo.id) }.forEach {
                    it.assertBooleanProperty(property, false)
                }
            }

            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - All words should be toggled even if one word does not exist`(property: WordToggleableProperty) {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                )

                wordEntities.forEach {
                    it.updateBooleanProperty(property, false)
                }

                val request = wordRequestFactory.togglePropertyForMultipleWordsRequest(
                    authenticatedUser = authenticatedUser,
                    wordEntities = wordEntities + wordSeeder.seedOneEntity(),
                    property = property
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()

                }

                wordEntities.map { wordService.findByIdOrFail(it.id, authenticatedUser.userInfo.id) }.forEach {
                    it.assertBooleanProperty(property, true)
                }
            }

            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - Word of another user should no te toggled`(property: WordToggleableProperty) {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo
                )

                val wordsFromAnotherUser: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = anotherUser
                )

                (wordEntities + wordsFromAnotherUser).forEach {
                    it.updateBooleanProperty(property, true)
                }


                val request = wordRequestFactory.togglePropertyForMultipleWordsRequest(
                    authenticatedUser = authenticatedUser,
                    wordEntities = wordEntities + wordsFromAnotherUser,
                    property = property
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.OK.value()
                }

                // All words from authenticated user should be toggled
                wordEntities.map { wordService.findByIdOrFail(it.id, authenticatedUser.userInfo.id) }.forEach {
                    it.assertBooleanProperty(property, false)
                }

                // All words from another user should not be toggled
                wordsFromAnotherUser.map { wordService.findByIdOrFail(it.id, anotherUser.id) }.forEach {
                    it.assertBooleanProperty(property, true)
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Anonymous user cannot toggle words' property`() {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo
                )

                val request = wordRequestFactory.togglePropertyForMultipleWordsRequest(
                    wordEntities = wordEntities,
                    authenticatedUser = null
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.FORBIDDEN.value()
                }
            }

            @Test
            fun `400 - not found when no words' ids are provided`() {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                )

                wordEntities.forEach {
                    it.updateBooleanProperty(WordToggleableProperty.IS_COMPLETED, false)
                }

                val request = wordRequestFactory.togglePropertyForMultipleWordsRequest(
                    authenticatedUser = authenticatedUser,
                    wordEntities = listOf(),
                    property = WordToggleableProperty.IS_COMPLETED
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `400 - cannot change non boolean property for multiple words`() {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = authenticatedUser.userInfo,
                )

                wordEntities.forEach {
                    it.updateBooleanProperty(WordToggleableProperty.IS_COMPLETED, false)
                }

                val request = wordRequestFactory.togglePropertyForMultipleWordsRequest(
                    authenticatedUser = authenticatedUser,
                    wordEntities = wordEntities,
                    property = null
                )

                request.param("property", "not_boolean")

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.BAD_REQUEST.value()
                }
            }

            @Test
            fun `404 - when all words do not belong to the user`() {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    user = anotherUser
                )

                val request = wordRequestFactory.togglePropertyForMultipleWordsRequest(
                    authenticatedUser = authenticatedUser,
                    wordEntities = wordEntities,
                    property = WordToggleableProperty.IS_COMPLETED
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }

            @Test
            fun `404 - when all words do not exist`() {
                val wordEntities: List<WordEntity> = List(5, init = { wordMockFactory.mockEntity() })

                val request = wordRequestFactory.togglePropertyForMultipleWordsRequest(
                    authenticatedUser = authenticatedUser,
                    wordEntities = wordEntities,
                    property = WordToggleableProperty.IS_COMPLETED
                )

                mockMvc.perform(request).andReturn().let {
                    it.response.status shouldBe HttpStatus.NOT_FOUND.value()
                }
            }
        }

    }
     */

//    private fun assertThatWordActuallyExists(
//        response: APIClientResponse<WordDTO>,
//        authenticatedUser: MockedAuthenticatedUser
//    ): WordEntity {
//        assertNotNull(response.body)
//
//        assertEquals(authenticatedUser.userInfo.id, response.body!!.userId)
//
//        val valueSavedInDatabase: WordEntity? = wordRepository.findByIdAndUserId(
//            id = response.body.id,
//            userId = authenticatedUser.userInfo.id
//        ).block()
//
//        assertNotNull(valueSavedInDatabase)
//
//        valueSavedInDatabase!!.compareWith(
//            wordMapper.toEntity(response.body)
//        )
//
//        return valueSavedInDatabase
//    }
//
//    private fun assertThatBankActuallyExists(
//        bankToVerify: BankEntity?,
//    ): BankEntity {
//        assertNotNull(bankToVerify)
//
//        return bankService
//            .findById(id = bankToVerify!!.id!!)
//            .map { bank ->
//                assertNotNull(bank)
//
//                bank!!
//            }
//            .block()!!
//    }

    // Register a utility function to assert the boolean property of a word
    private fun WordEntity.assertBooleanProperty(property: WordToggleableProperty, expectedValue: Boolean) {
        when (property) {
            WordToggleableProperty.IS_COMPLETED -> isCompleted shouldBe expectedValue
            WordToggleableProperty.IS_BOOKMARKED -> isBookmarked shouldBe expectedValue
        }
    }

    // Register a utility function to update the boolean property of a word and also assert its new value
    private fun WordEntity.updateBooleanProperty(property: WordToggleableProperty, value: Boolean) {
        when (property) {
            WordToggleableProperty.IS_COMPLETED -> isCompleted = value
            WordToggleableProperty.IS_BOOKMARKED -> isBookmarked = value
        }

        wordRepository.save(this).block()

        this.assertBooleanProperty(property, value)
    }
}
