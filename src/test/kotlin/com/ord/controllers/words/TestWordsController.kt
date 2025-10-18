package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.security.UserRepository
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.dto.*
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.requests.enums.WordToggleableProperty
import com.ord.core.word.api.responses.dto.SingleWordResponse
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.model.WordDTO
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.core.word.repository.WordRepository
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.features.bank.repository.BankRepository
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("- WordsController")
class TestWordsController @Autowired constructor(
    private val wordRepository: WordRepository,
    private val bankRepository: BankRepository,
    private val bankSeeder: BankSeeder,
    private val bankService: BankService,
    private val userSeeder: UserSeeder,
    private val wordSeeder: WordSeeder,
    private val bankGroupSeeder: BankGroupSeeder,
    private var wordMockFactory: WordFactory,

    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder

) : ControllerTestBase(
    webClient = webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder
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
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)

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
                val word = wordSeeder.seedOneEntityForUser(anotherUser.id!!)

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


    @Nested
    @DisplayName("[POST] /api/v1/words/ - create a word")
    inner class CreateWordTests {
        private fun createDefaultWordRequest(
            bankId: UUID? = null,
            bankToCreate: CreateBankRequest? = null,
            extraMark: WordExtraMark? = WordExtraMark.SLANG,
            translatedTo: LanguageName? = LanguageName.POLISH
        ): CreateWordRequest {
            return CreateWordRequest(
                origin = "word in english",
                translation = "slowo po polsku",
                definition = "definition",
                type = WordType.NOUN,
                extraMark = extraMark,
                translatedTo = translatedTo,
                translatedFrom = LanguageName.ENGLISH,
                bankId = bankId,
                bankToCreate = bankToCreate
            )
        }

        private fun assertWordCreatedSuccessfully(
            response: APIClientResponse<WordDTO?>,
            expectedBankId: UUID? = null
        ): WordEntity {
            response.status shouldBe HttpStatus.CREATED
            response.body shouldNotBe null

            val createdWord = response.body!!
            createdWord.userId shouldBe authenticatedUser.userInfo.id

            // Verify word exists in database
            val wordEntity = wordRepository.findByIdAndUserId(
                id = createdWord.id,
                userId = authenticatedUser.userInfo.id
            ).block()

            wordEntity shouldNotBe null
            wordEntity!!.origin shouldBe "word in english"
            wordEntity.translation shouldBe "slowo po polsku"
            wordEntity.definition shouldBe "definition"

            if (expectedBankId != null) {
                wordEntity.bankId shouldBe expectedBankId
            }

            return wordEntity
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - Word can be created without bank being specified`() {
                val request = createDefaultWordRequest()
                val response = wordsAPIClient.createWord(request, user = authenticatedUser)

                assertWordCreatedSuccessfully(response)
            }


            @Test
            fun `201 - Word can be created and assigned to an existing bank`() {
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = createDefaultWordRequest(bankId = bank.id)
                val response = wordsAPIClient.createWord(request, user = authenticatedUser)

                assertWordCreatedSuccessfully(response, expectedBankId = bank.id)
            }

            @Test
            fun `201 - Word and a bank can be created at the same time`() {
                val request = createDefaultWordRequest(
                    bankToCreate = CreateBankRequest(
                        name = "Test Bank",
                        description = "Test Bank Description"
                    )
                )
                val response = wordsAPIClient.createWord(request, user = authenticatedUser)

                val wordEntity = assertWordCreatedSuccessfully(response)

                wordEntity.bankId shouldNotBe null

                val createdBank = bankService.findByIdOrFail(
                    id = wordEntity.bankId!!,
                    userId = authenticatedUser.userInfo.id
                ).block()

                createdBank shouldNotBe null
            }

            @Test
            fun `201 - Word can be create with no extra mark specified`() {
                val request = createDefaultWordRequest(extraMark = null)
                val response = wordsAPIClient.createWord(request, user = authenticatedUser)

                assertWordCreatedSuccessfully(response)
                response.body!!.extraMark shouldBe null
            }

            @Test
            fun `201 - Word can be created with no translated to language specified defaulting to the user's native language`() {
                val request = createDefaultWordRequest(translatedTo = null)

                val response = wordsAPIClient.createWord(request, user = authenticatedUser)

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.translatedTo shouldBe authenticatedUser.userInfo.nativeLanguage
            }

            @Test
            fun `201 - Word can be created even with bank name identical to another bank name but for different user`() {
                val anotherUser = userSeeder.seedOneEntity()
                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val request = createDefaultWordRequest(
                    bankToCreate = CreateBankRequest(
                        name = bankOfAnotherUser.name,
                        description = "Test Bank Description"
                    )
                )
                val response = wordsAPIClient.createWord(request, user = authenticatedUser)

                val wordEntity = assertWordCreatedSuccessfully(response)

                // Verify bank was created with the same name for different user
                wordEntity.bankId shouldNotBe null
                val createdBank = bankService.findByIdOrFail(
                    id = wordEntity.bankId!!,
                    userId = authenticatedUser.userInfo.id
                ).block()
                createdBank shouldNotBe null
                createdBank!!.name shouldBe bankOfAnotherUser.name
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `403 - Anonymous user cannot create a word`() {
                val request = createDefaultWordRequest()
                val response = wordsAPIClient.createWord(request, user = null)

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - Word cannot be created with bankId and bankToCreate at the same time`() {
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = createDefaultWordRequest().copy(
                    bankId = bank.id,
                    bankToCreate = CreateBankRequest(
                        name = "Test Bank",
                        description = "Test Bank Description"
                    )
                )

                val response = wordsAPIClient.createWord(request, user = authenticatedUser)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Word cannot be created with bankToCreate name matching already existing bank name`() {
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val request = createDefaultWordRequest().copy(
                    bankToCreate = CreateBankRequest(
                        name = bank.name,
                        description = "Test Bank Description"
                    )
                )

                val response = wordsAPIClient.createWord(request, user = authenticatedUser)

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `404 - Word cannot be created with bankId referring to a bank of another user`() {
                val anotherUser = userSeeder.seedOneEntity()
                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val request = createDefaultWordRequest(
                    bankId = bankOfAnotherUser.id
                )

                val response = wordsAPIClient.createWord(request, user = authenticatedUser)

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }


    @Nested
    @DisplayName("[PATCH] /api/v1/words/{id} - update a word")
    inner class UpdateWordTests {
        private fun createDefaultUpdateWordRequest(
            bankId: UUID? = null,
            bankToCreate: CreateBankRequest? = null
        ): UpdateWordRequest {
            return UpdateWordRequest(
                origin = "UPDATED word in foreign language",
                translation = "UPDATED word in native language",
                definition = "UPDATED definition",
                type = WordType.VERB,
                extraMark = WordExtraMark.SLANG,
                translatedTo = LanguageName.SLOVENIAN,
                translatedFrom = LanguageName.NORWEGIAN,
                bankId = bankId,
                bankToCreate = bankToCreate
            )
        }

        private fun assertWordUpdatedSuccessfully(
            response: APIClientResponse<WordDTO?>,
            originalWord: WordEntity,
            updateRequest: UpdateWordRequest
        ): WordEntity {
            response.status shouldBe HttpStatus.OK
            response.body shouldNotBe null

            val updatedWord = response.body!!
            updatedWord.userId shouldBe authenticatedUser.userInfo.id
            updatedWord.id shouldBe originalWord.id

            // Verify word exists in database
            val wordEntity = wordRepository.findByIdAndUserId(
                id = updatedWord.id,
                userId = authenticatedUser.userInfo.id
            ).block()

            wordEntity shouldNotBe null

            // Check the updated fields
            wordEntity!!.origin shouldBe (updateRequest.origin ?: originalWord.origin)
            wordEntity.translation shouldBe (updateRequest.translation ?: originalWord.translation)
            wordEntity.definition shouldBe (updateRequest.definition ?: originalWord.definition)
            wordEntity.type shouldBe (updateRequest.type ?: originalWord.type)
            wordEntity.extraMark shouldBe (updateRequest.extraMark ?: originalWord.extraMark)
            wordEntity.translatedTo shouldBe (updateRequest.translatedTo ?: originalWord.translatedTo)
            wordEntity.translatedFrom shouldBe (updateRequest.translatedFrom ?: originalWord.translatedFrom)

            return wordEntity
        }

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - Word can be updated`() {
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val updateRequest = createDefaultUpdateWordRequest()

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = authenticatedUser
                )

                assertWordUpdatedSuccessfully(response, word, updateRequest)
            }

            @Test
            fun `200 - Word can be updated with bankToCreate name identical to another bank name but for different user`() {
                val anotherUser = userSeeder.seedOneEntity()
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val updateRequest = createDefaultUpdateWordRequest(
                    bankToCreate = CreateBankRequest(
                        name = bankOfAnotherUser.name,
                        description = "Test Bank Description"
                    )
                )

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = authenticatedUser
                )

                val updatedWordEntity = assertWordUpdatedSuccessfully(response, word, updateRequest)

                // Verify bank was created with the same name for different user
                updatedWordEntity.bankId shouldNotBe null
                val createdBank = bankService.findByIdOrFail(
                    id = updatedWordEntity.bankId!!,
                    userId = authenticatedUser.userInfo.id
                ).block()
                createdBank shouldNotBe null
                createdBank!!.name shouldBe bankOfAnotherUser.name
            }

            @Test
            fun `200 - Word can be updated with only one field being specified`() {
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val updateRequest = UpdateWordRequest(origin = "new origin")

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = authenticatedUser
                )

                assertWordUpdatedSuccessfully(response, word, updateRequest)
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - Anonymous user cannot update a word`() {
                val word = wordSeeder.seedOneEntity()
                val updateRequest = createDefaultUpdateWordRequest()

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - Word cannot be updated with bankId and bankToCreate at the same time`() {
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val updateRequest = createDefaultUpdateWordRequest().copy(
                    bankId = bank.id,
                    bankToCreate = CreateBankRequest(
                        name = "Test Bank",
                        description = "Test Bank Description"
                    )
                )

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Word cannot be updated with bankToCreate name matching already existing bank name`() {
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val updateRequest = createDefaultUpdateWordRequest().copy(
                    bankToCreate = CreateBankRequest(
                        name = bank.name,
                        description = "Test Bank Description"
                    )
                )

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `404 - Word cannot be updated with bankId referring to a bank of another user`() {
                val anotherUser = userSeeder.seedOneEntity()
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val updateRequest = createDefaultUpdateWordRequest(bankId = bankOfAnotherUser.id)

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - Word cannot be updated with bankId referring to a bank that does not exist`() {
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val updateRequest = createDefaultUpdateWordRequest(bankId = UUID.randomUUID())

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `400 - Word cannot be updated with bankToCreate name that is empty`() {
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val updateRequest = createDefaultUpdateWordRequest().copy(
                    bankToCreate = CreateBankRequest(
                        name = "",
                        description = "Test Bank Description"
                    )
                )

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `404 - Word cannot be updated by other user than the one who created it`() {
                val anotherUser = userSeeder.seedOneEntity()
                val word = wordSeeder.seedOneEntityForUser(anotherUser.id!!)
                val updateRequest = createDefaultUpdateWordRequest()

                val response = wordsAPIClient.updateWord(
                    id = word.id!!,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
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
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id, bankId = null)

                // Verify word exists before deletion
                val wordBeforeDelete = wordRepository.findByIdAndUserId(
                    id = word.id!!,
                    userId = authenticatedUser.userInfo.id
                ).block()
                wordBeforeDelete shouldNotBe null

                val response = wordsAPIClient.deleteWord(
                    id = word.id,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify word was deleted
                val wordAfterDelete = wordRepository.findByIdAndUserId(
                    id = word.id,
                    userId = authenticatedUser.userInfo.id
                ).block()
                wordAfterDelete shouldBe null
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - Word cannot be deleted by an anonymous user`() {
                val word = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id, bankId = null)

                // Verify word exists before deletion attempt
                val wordBeforeDelete = wordRepository.findByIdAndUserId(
                    id = word.id!!,
                    userId = authenticatedUser.userInfo.id
                ).block()
                wordBeforeDelete shouldNotBe null

                val response = wordsAPIClient.deleteWord(
                    id = word.id,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED

                // Verify word still exists after failed deletion
                val wordAfterDelete = wordRepository.findByIdAndUserId(
                    id = word.id,
                    userId = authenticatedUser.userInfo.id
                ).block()
                wordAfterDelete shouldNotBe null
            }

            @Test
            fun `404 - Word can be deleted only by its owner`() {
                val anotherUser = userSeeder.seedOneEntity()
                val word = wordSeeder.seedOneEntityForUser(anotherUser.id!!, bankId = null)

                // Verify word exists for the other user
                val wordBeforeDelete = wordRepository.findByIdAndUserId(
                    id = word.id!!,
                    userId = anotherUser.id
                ).block()
                wordBeforeDelete shouldNotBe null

                val response = wordsAPIClient.deleteWord(
                    id = word.id,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND

                // Verify word still exists for the other user
                val wordAfterDelete = wordRepository.findByIdAndUserId(
                    id = word.id,
                    userId = anotherUser.id
                ).block()
                wordAfterDelete shouldNotBe null
            }

            @Test
            fun `404 - Cannot remove non existing word`() {
                val response = wordsAPIClient.deleteWord(
                    id = UUID.randomUUID(),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
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
                val firstBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val secondBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val wordEntity = wordSeeder.seedOneEntityForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = firstBank.id
                )

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id!!,
                    body = ChangeBankForSingleWordRequest(bankId = secondBank.id),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                // Verify word's bank was changed in database
                val updatedWord = wordRepository.findByIdAndUserId(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).block()
                updatedWord shouldNotBe null
                updatedWord!!.bankId shouldBe secondBank.id
            }

            @Test
            fun `200 - Word's bank can be changed to newly created bank`() {
                val newBankName = "NEW_EXTRA_BANK_NAME"
                val initialBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val wordEntity = wordSeeder.seedOneEntityForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = initialBank.id
                )
                initialBank.name shouldNotBe newBankName

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id!!,
                    body = ChangeBankForSingleWordRequest(
                        bankToCreate = CreateBankRequest(
                            name = newBankName,
                            description = "x".repeat(64)
                        )
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                // Verify word's bank was changed to new bank in database
                val updatedWord = wordRepository.findByIdAndUserId(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).block()
                updatedWord shouldNotBe null
                updatedWord!!.bankId shouldNotBe initialBank.id
                updatedWord.bankId shouldNotBe null
                // Verify new bank was created with correct name
                val newBank = bankRepository.findByIdAndUserId(
                    id = updatedWord.bankId!!,
                    userId = authenticatedUser.userInfo.id
                ).block()
                newBank shouldNotBe null
                newBank!!.name shouldBe newBankName
            }

            @Test
            fun `200 - Word's bank can be change from null to already existing bank`() {
                val wordEntity = wordSeeder.seedOneEntityForUser(
                    userId = authenticatedUser.userInfo.id
                )
                val newBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                // Verify word initially has no bank
                val wordBeforeChange = wordRepository.findByIdAndUserId(
                    id = wordEntity.id!!,
                    userId = authenticatedUser.userInfo.id
                ).block()
                wordBeforeChange shouldNotBe null
                wordBeforeChange!!.bankId shouldBe null

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id,
                    body = ChangeBankForSingleWordRequest(bankId = newBank.id),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                // Verify word's bank was set
                val updatedWord = wordRepository.findByIdAndUserId(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).block()
                updatedWord shouldNotBe null
                updatedWord!!.bankId shouldBe newBank.id
            }

            @Test
            fun `200 - Word's bank can be change from null to newly created bank`() {
                val newBankName = "NEW_EXTRA_BANK_NAME"
                val wordEntity = wordSeeder.seedOneEntityForUser(
                    userId = authenticatedUser.userInfo.id
                )

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id!!,
                    body = ChangeBankForSingleWordRequest(
                        bankToCreate = CreateBankRequest(
                            name = newBankName,
                            description = "x".repeat(64)
                        )
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                // Verify word's bank was set to new bank
                val updatedWord = wordRepository.findByIdAndUserId(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).block()
                updatedWord shouldNotBe null
                updatedWord!!.bankId shouldNotBe null
                // Verify new bank was created with correct name
                val newBank = bankRepository.findByIdAndUserId(
                    id = updatedWord.bankId!!,
                    userId = authenticatedUser.userInfo.id
                ).block()
                newBank shouldNotBe null
                newBank!!.name shouldBe newBankName
            }

            @Test
            fun `200 - Word's bank can be unassigned`() {
                val initialBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val wordEntity = wordSeeder.seedOneEntityForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = initialBank.id
                )
                // Verify word initially has a bank
                val wordBeforeChange = wordRepository.findByIdAndUserId(
                    id = wordEntity.id!!,
                    userId = authenticatedUser.userInfo.id
                ).block()
                wordBeforeChange shouldNotBe null
                wordBeforeChange!!.bankId shouldBe initialBank.id

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id,
                    body = ChangeBankForSingleWordRequest(),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                // Verify word's bank was unassigned
                val updatedWord = wordRepository.findByIdAndUserId(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).block()
                updatedWord shouldNotBe null
                updatedWord!!.bankId shouldBe null
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - Anonymous user cannot change word's bank`() {
                val wordEntity = wordSeeder.seedOneEntity()

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id!!,
                    body = ChangeBankForSingleWordRequest(),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - Word's bank cannot be changed by other user than the one who created it`() {
                val anotherUser = userSeeder.seedOneEntity()
                val wordEntity = wordSeeder.seedOneEntityForUser(anotherUser.id!!)

                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id!!,
                    body = ChangeBankForSingleWordRequest(
                        bankId = bankOfAnotherUser.id
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - Word's bank cannot be changed if word does not exist`() {
                val response = wordsAPIClient.changeWordBank(
                    id = UUID.randomUUID(),
                    body = ChangeBankForSingleWordRequest(),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - Word's bank cannot be changed if bank does not exist`() {
                val wordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id!!,
                    body = ChangeBankForSingleWordRequest(bankId = UUID.randomUUID()),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - Word's bank cannot be changed if bank does not belong to the user`() {
                val anotherUser = userSeeder.seedOneEntity()
                val wordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val bank = bankSeeder.seedOneEntityForUser(anotherUser)

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id!!,
                    body = ChangeBankForSingleWordRequest(bankId = bank.id),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `400 - Word's bank cannot be changed if both bankId and bankToCreate are specified`() {
                val wordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val response = wordsAPIClient.changeWordBank(
                    id = wordEntity.id!!,
                    body = ChangeBankForSingleWordRequest(
                        bankId = bank.id,
                        bankToCreate = CreateBankRequest(
                            name = "Test Bank",
                            description = "Test Description"
                        )
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
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
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    amount = 5
                )

                // Verify words initially have no bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe null
                }

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankId = bank.id,
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify all words now have the correct bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe bank.id
                }
            }

            @Test
            fun `200 - Words' bank can be changed from null to a newly created bank`() {
                val bankName = "NEW_EXTRA_BANK_NAME"

                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    amount = 5
                )

                // Verify words initially have no bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe null
                }

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankToCreate = CreateBankRequest(
                            name = bankName,
                            description = "x".repeat(64)
                        ),
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify all words now have the correct bank and the bank was created
                var createdBankId: UUID? = null
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldNotBe null

                    if (createdBankId == null) {
                        createdBankId = word.bankId
                        val createdBank = bankRepository.findByIdAndUserId(
                            id = word.bankId!!,
                            userId = authenticatedUser.userInfo.id
                        ).block()
                        createdBank shouldNotBe null
                        createdBank!!.name shouldBe bankName
                    } else {
                        word.bankId shouldBe createdBankId
                    }
                }
            }

            @Test
            fun `200 - Words' bank can be changed from an existing bank to another existing bank`() {
                val firstBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val secondBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = firstBank.id,
                    amount = 5
                )

                // Verify words initially have the first bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe firstBank.id
                }

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankId = secondBank.id,
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify all words now have the second bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe secondBank.id
                }
            }

            @Test
            fun `200 - Words' bank can be changed from an existing bank to a newly created bank`() {
                val newBankName = "NEW_EXTRA_BANK_NAME"
                val firstBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = firstBank.id,
                    amount = 5
                )

                // Verify words initially have the first bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe firstBank.id
                }

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankToCreate = CreateBankRequest(
                            name = newBankName,
                            description = "x".repeat(64)
                        ),
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify all words now have the new bank and the bank was created
                var createdBankId: UUID? = null
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldNotBe null
                    word.bankId shouldNotBe firstBank.id

                    if (createdBankId == null) {
                        createdBankId = word.bankId
                        val createdBank = bankRepository.findByIdAndUserId(
                            id = word.bankId!!,
                            userId = authenticatedUser.userInfo.id
                        ).block()
                        createdBank shouldNotBe null
                        createdBank!!.name shouldBe newBankName
                    } else {
                        word.bankId shouldBe createdBankId
                    }
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - Anonymous user cannot change words' bank`() {
                val anotherUser = userSeeder.seedOneEntity()
                val initialBank = bankSeeder.seedOneEntityForUser(anotherUser)
                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = anotherUser.id!!,
                    bankId = initialBank.id,
                    amount = 3
                )

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED

                // Verify words still have the original bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = anotherUser.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed by other user than the one who created them`() {
                val anotherUser = userSeeder.seedOneEntity()
                val initialBank = bankSeeder.seedOneEntityForUser(anotherUser)
                val bankToChange = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = anotherUser.id!!,
                    bankId = initialBank.id,
                    amount = 3
                )

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankId = bankToChange.id,
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND

                // Verify words still have the original bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = anotherUser.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed if bank does not exist`() {
                val initialBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = initialBank.id,
                    amount = 3
                )

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankId = UUID.randomUUID(),
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND

                // Verify words still have the original bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe initialBank.id
                }
            }

            @Test
            fun `404 - Words' bank cannot be changed if bank does not belong to the user`() {
                val anotherUser = userSeeder.seedOneEntity()
                val initialBank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)
                val bankOfAnotherUser = bankSeeder.seedOneEntityForUser(anotherUser)

                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    bankId = initialBank.id,
                    amount = 3
                )

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankId = bankOfAnotherUser.id,
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND

                // Verify words still have the original bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe initialBank.id
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if both bankId and bankToCreate are specified`() {
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    amount = 3
                )

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankId = bank.id,
                        bankToCreate = CreateBankRequest(
                            name = "Test Bank",
                            description = "Test Description"
                        ),
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST

                // Verify words still have no bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate name is empty`() {
                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    amount = 3
                )

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankToCreate = CreateBankRequest(
                            name = "",
                            description = "Test Description"
                        ),
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST

                // Verify words still have no bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate name is identical to already existing bank name`() {
                val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    amount = 3
                )

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankToCreate = CreateBankRequest(
                            name = bank.name,
                            description = "Test Description"
                        ),
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST

                // Verify words still have no bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate description is longer than 255 characters`() {
                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    amount = 3
                )

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankToCreate = CreateBankRequest(
                            name = "Test Bank",
                            description = "x".repeat(256)
                        ),
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST

                // Verify words still have no bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe null
                }
            }

            @Test
            fun `400 - Words' bank cannot be changed if bankToCreate description is empty`() {
                val wordEntities = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                    amount = 3
                )

                val response = wordsAPIClient.changeBankForMultipleWords(
                    body = ChangeBankForMultipleWordsRequest(
                        bankToCreate = CreateBankRequest(
                            name = "Test Bank",
                            description = ""
                        ),
                        wordIds = wordEntities.map { it.id!! }
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST

                // Verify words still have no bank
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.bankId shouldBe null
                }
            }
        }
    }


    @Nested
    @DisplayName("[POST] /api/v1/words/{id}/toggle-property - toggle word's property")
    inner class TogglePropertyForOneWordTests {
        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - Word's boolean properties can be toggled from false to true`(property: WordToggleableProperty) {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)

                wordEntity.updateBooleanProperty(property, false)

                val response = wordsAPIClient.togglePropertyForOneWord(
                    id = wordEntity.id!!,
                    property = property,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify word property was toggled in database
                val word = wordRepository.findByIdAndUserId(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).block()
                word shouldNotBe null
                word!!.assertBooleanProperty(property, true)
            }

            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - Word's boolean properties can be toggled from true to false`(property: WordToggleableProperty) {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)

                wordEntity.updateBooleanProperty(property, true)

                val response = wordsAPIClient.togglePropertyForOneWord(
                    id = wordEntity.id!!,
                    property = property,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify word property was toggled in database
                val word = wordRepository.findByIdAndUserId(
                    id = wordEntity.id,
                    userId = authenticatedUser.userInfo.id
                ).block()
                word shouldNotBe null
                word!!.assertBooleanProperty(property, false)
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - Anonymous user cannot toggle word's property`() {
                val wordEntity: WordEntity = wordSeeder.seedOneEntity()

                val response = wordsAPIClient.togglePropertyForOneWord(
                    id = wordEntity.id!!,
                    property = WordToggleableProperty.IS_BOOKMARKED,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - Word's property cannot be toggled by other user than the one who created it`() {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(anotherUser.id!!)

                val response = wordsAPIClient.togglePropertyForOneWord(
                    id = wordEntity.id!!,
                    property = WordToggleableProperty.IS_BOOKMARKED,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - Word's property cannot be toggled if word does not exist`() {
                val response = wordsAPIClient.togglePropertyForOneWord(
                    id = UUID.randomUUID(),
                    property = WordToggleableProperty.IS_BOOKMARKED,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `400 - Word's property cannot be toggled if property is not provided`() {
                val wordEntity: WordEntity = wordSeeder.seedOneEntityForUser(authenticatedUser.userInfo.id)

                val response = wordsAPIClient.togglePropertyForOneWord(
                    id = wordEntity.id!!,
                    property = null,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }


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
                    userId = authenticatedUser.userInfo.id,
                )

                wordEntities.forEach {
                    it.updateBooleanProperty(property, false)
                }

                val response = wordsAPIClient.togglePropertyForManyWords(
                    property = property,
                    body = WordBulkActionRequest(ids = wordEntities.map { it.id!! }),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify all words are toggled
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.assertBooleanProperty(property, true)
                }
            }

            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - Words' boolean properties can be toggled from true to false`(property: WordToggleableProperty) {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                )

                wordEntities.forEach {
                    it.updateBooleanProperty(property, true)
                }

                val response = wordsAPIClient.togglePropertyForManyWords(
                    property = property,
                    body = WordBulkActionRequest(ids = wordEntities.map { it.id!! }),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify all words are toggled
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.assertBooleanProperty(property, false)
                }
            }

            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - All words should be toggled even if one word does not exist`(property: WordToggleableProperty) {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                )

                wordEntities.forEach {
                    it.updateBooleanProperty(property, false)
                }

                val nonExistentWordEntity = wordSeeder.seedOneEntity()

                val response = wordsAPIClient.togglePropertyForManyWords(
                    property = property,
                    body = WordBulkActionRequest(ids = wordEntities.map { it.id!! } + nonExistentWordEntity.id!!),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // Verify existing words are toggled
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.assertBooleanProperty(property, true)
                }
            }

            @ParameterizedTest
            @EnumSource(WordToggleableProperty::class)
            fun `200 - Word of another user should not be toggled`(property: WordToggleableProperty) {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id
                )

                val wordsFromAnotherUser: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    userId = anotherUser.id!!
                )

                (wordEntities + wordsFromAnotherUser).forEach {
                    it.updateBooleanProperty(property, true)
                }

                val response = wordsAPIClient.togglePropertyForManyWords(
                    property = property,
                    body = WordBulkActionRequest(ids = (wordEntities + wordsFromAnotherUser).map { it.id!! }),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK

                // All words from authenticated user should be toggled
                wordEntities.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = authenticatedUser.userInfo.id
                    ).block()
                    word shouldNotBe null
                    word!!.assertBooleanProperty(property, false)
                }

                // All words from another user should not be toggled
                wordsFromAnotherUser.forEach { wordEntity ->
                    val word = wordRepository.findByIdAndUserId(
                        id = wordEntity.id!!,
                        userId = anotherUser.id
                    ).block()
                    word shouldNotBe null
                    word!!.assertBooleanProperty(property, true)
                }
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - Anonymous user cannot toggle words' property`() {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id
                )

                val response = wordsAPIClient.togglePropertyForManyWords(
                    property = WordToggleableProperty.IS_BOOKMARKED,
                    body = WordBulkActionRequest(ids = wordEntities.map { it.id!! }),
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - Bad request when no words' ids are provided`() {
                val response = wordsAPIClient.togglePropertyForManyWords(
                    property = WordToggleableProperty.IS_BOOKMARKED,
                    body = WordBulkActionRequest(ids = emptyList()),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - Cannot change non boolean property for multiple words`() {
                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    userId = authenticatedUser.userInfo.id,
                )

                wordEntities.forEach {
                    it.updateBooleanProperty(WordToggleableProperty.IS_BOOKMARKED, false)
                }

                // Test with null property to trigger BAD_REQUEST
                val response = wordsAPIClient.togglePropertyForManyWords(
                    property = null,
                    body = WordBulkActionRequest(ids = wordEntities.map { it.id!! }),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `404 - When all words do not belong to the user`() {
                val anotherUser: UserEntity = userSeeder.seedOneEntity()

                val wordEntities: List<WordEntity> = wordSeeder.seedMultipleEntitiesForUser(
                    userId = anotherUser.id!!
                )

                val response = wordsAPIClient.togglePropertyForManyWords(
                    property = WordToggleableProperty.IS_BOOKMARKED,
                    body = WordBulkActionRequest(ids = wordEntities.map { it.id!! }),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - When all words do not exist`() {
                val nonExistentIds = List(5) { UUID.randomUUID() }

                val response = wordsAPIClient.togglePropertyForManyWords(
                    property = WordToggleableProperty.IS_BOOKMARKED,
                    body = WordBulkActionRequest(ids = nonExistentIds),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }

    // Register a utility function to assert the boolean property of a word
    private fun WordEntity.assertBooleanProperty(property: WordToggleableProperty, expectedValue: Boolean) {
        when (property) {
            WordToggleableProperty.IS_BOOKMARKED -> isBookmarked shouldBe expectedValue
        }
    }

    // Register a utility function to update the boolean property of a word and also assert its new value
    private fun WordEntity.updateBooleanProperty(property: WordToggleableProperty, value: Boolean) {
        when (property) {
            WordToggleableProperty.IS_BOOKMARKED -> isBookmarked = value
        }

        wordRepository.save(this).block()

        this.assertBooleanProperty(property, value)
    }
}