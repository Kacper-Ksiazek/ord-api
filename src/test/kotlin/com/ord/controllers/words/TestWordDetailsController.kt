package com.ord.controllers.words

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.core.word.api.details.requests.dto.CreateWordDetailsRequest
import com.ord.core.word.api.details.requests.dto.UpdateWordDetailsRequest
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_details.WordDetailsMapper
import com.ord.core.word.models.word_details.enums.WordCollocationFrequency
import com.ord.core.word.models.word_details.enums.WordGender
import com.ord.core.word.models.word_details.jsonb.ExampleSentence
import com.ord.core.word.models.word_details.jsonb.WordCollocation
import com.ord.core.word.models.word_details.jsonb.WordGrammar
import com.ord.core.word.models.word_details.jsonb.WordPronunciation
import com.ord.core.word.repositories.WordDetailsRepository
import com.ord.core.word.repositories.WordRepository
import com.ord.testing_utils.api.clients.WordDetailsAPIClient
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DisplayName("- WordDetailsController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TestWordDetailsController @Autowired constructor(
    private val wordRepository: WordRepository,
    private val wordDetailsRepository: WordDetailsRepository,
    private val wordDetailsMapper: WordDetailsMapper,
    webClient: WebTestClient,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
) : ControllerTestBase(
    webClient,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder
) {
    private val apiClient = WordDetailsAPIClient(webClient)

    private fun createTestWord(userId: UUID, origin: String = "test"): UUID {
        val word = wordRepository.save(
            WordEntity(
                type = WordType.NOUN,
                origin = origin,
                translation = "translation",
                definition = "definition",
                translatedFrom = LanguageName.ENGLISH,
                translatedTo = LanguageName.SPANISH,
                userId = userId
            )
        ).block()!!

        return word.id!!
    }

    @Nested
    @DisplayName("[POST] /api/v1/words/{wordId}/details - create word details")
    inner class CreateWordDetailsTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `201 - should create word details with all fields`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "hello")

                val request = CreateWordDetailsRequest(
                    useCases = setOf("greeting", "informal"),
                    synonyms = setOf("hi", "hey"),
                    antonyms = setOf("goodbye", "bye"),
                    commonMistakes = setOf("Don't use in formal situations"),
                    exampleSentences = setOf(
                        ExampleSentence(
                            context = "Meeting a friend",
                            sentence = "Hello! How are you?",
                            translation = "¡Hola! ¿Cómo estás?"
                        )
                    ),
                    collocations = setOf(
                        WordCollocation(
                            phrase = "say hello",
                            translation = "decir hola",
                            frequency = WordCollocationFrequency.VERY_COMMON
                        )
                    ),
                    pronunciation = WordPronunciation(
                        ipa = "həˈləʊ",
                        syllables = "he-llo",
                        stress = 2
                    ),
                    grammar = WordGrammar(
                        gender = WordGender.NEUTER,
                        definiteArticle = null,
                        pluralForm = null
                    ),
                    culturalNotes = "Common greeting in English",
                    learningTips = "Remember the stress on the second syllable"
                )

                val response = apiClient.createWordDetails(
                    wordId = wordId,
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.wordId shouldBe wordId
                response.body.userId shouldBe authenticatedUser.userInfo.id
                response.body.useCases shouldBe setOf("greeting", "informal")
                response.body.synonyms shouldBe setOf("hi", "hey")
                response.body.antonyms shouldBe setOf("goodbye", "bye")
                response.body.commonMistakes shouldBe setOf("Don't use in formal situations")
                response.body.exampleSentences.size shouldBe 1
                response.body.collocations.size shouldBe 1
                response.body.pronunciation shouldNotBe null
                response.body.pronunciation!!.ipa shouldBe "həˈləʊ"
                response.body.grammar shouldNotBe null
                response.body.culturalNotes shouldBe "Common greeting in English"
                response.body.learningTips shouldBe "Remember the stress on the second syllable"

                // Verify it was saved in database
                val savedEntity = wordDetailsRepository.findByWordIdAndUserId(
                    wordId = wordId,
                    userId = authenticatedUser.userInfo.id
                ).block()

                savedEntity shouldNotBe null
                val savedDto = wordDetailsMapper.toDTO(savedEntity!!)
                savedDto.useCases shouldBe setOf("greeting", "informal")
            }

            @Test
            fun `201 - should create word details with minimal fields`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "world")

                val request = CreateWordDetailsRequest(
                    useCases = setOf("noun"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(
                        ExampleSentence(
                            context = null,
                            sentence = "The world is beautiful",
                            translation = "El mundo es hermoso"
                        )
                    ),
                    collocations = setOf(),
                    pronunciation = null,
                    grammar = null,
                    culturalNotes = null,
                    learningTips = null
                )

                val response = apiClient.createWordDetails(
                    wordId = wordId,
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.wordId shouldBe wordId
                response.body.useCases shouldBe setOf("noun")
                response.body.synonyms shouldBe setOf()
                response.body.pronunciation shouldBe null
                response.body.grammar shouldBe null
                response.body.culturalNotes shouldBe null
                response.body.learningTips shouldBe null
            }

            @Test
            fun `201 - should create word details with empty sets`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "empty")

                val request = CreateWordDetailsRequest(
                    useCases = setOf("test"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf(),
                    pronunciation = null,
                    grammar = null,
                    culturalNotes = null,
                    learningTips = null
                )

                val response = apiClient.createWordDetails(
                    wordId = wordId,
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.synonyms shouldBe setOf()
                response.body.antonyms shouldBe setOf()
                response.body.commonMistakes shouldBe setOf()
                response.body.exampleSentences shouldBe setOf()
                response.body.collocations shouldBe setOf()
            }

            @Test
            fun `201 - should create word details with multiple example sentences`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "run")

                val request = CreateWordDetailsRequest(
                    useCases = setOf("verb", "noun"),
                    synonyms = setOf("sprint", "jog"),
                    antonyms = setOf("walk", "stop"),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(
                        ExampleSentence(
                            context = "Exercise",
                            sentence = "I run every morning",
                            translation = "Corro todas las mañanas"
                        ),
                        ExampleSentence(
                            context = "Sports",
                            sentence = "She runs very fast",
                            translation = "Ella corre muy rápido"
                        ),
                        ExampleSentence(
                            context = null,
                            sentence = "They run a business",
                            translation = "Ellos dirigen un negocio"
                        )
                    ),
                    collocations = setOf(
                        WordCollocation(
                            phrase = "run a marathon",
                            translation = "correr un maratón",
                            frequency = WordCollocationFrequency.COMMON
                        ),
                        WordCollocation(
                            phrase = "run late",
                            translation = "llegar tarde",
                            frequency = WordCollocationFrequency.VERY_COMMON
                        )
                    )
                )

                val response = apiClient.createWordDetails(
                    wordId = wordId,
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.exampleSentences.size shouldBe 3
                response.body.collocations.size shouldBe 2
                response.body.useCases shouldBe setOf("verb", "noun")
            }

            @Test
            fun `201 - should create word details with complete grammar information`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "beautiful")

                val request = CreateWordDetailsRequest(
                    useCases = setOf("adjective"),
                    synonyms = setOf("pretty", "gorgeous"),
                    antonyms = setOf("ugly"),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf(),
                    grammar = WordGrammar(
                        gender = null,
                        comparativeForm = "more beautiful",
                        superlativeForm = "most beautiful",
                        pluralForm = null,
                        definiteArticle = null
                    ),
                    pronunciation = WordPronunciation(
                        ipa = "ˈbjuːtɪfl",
                        syllables = "beau-ti-ful",
                        stress = 1
                    )
                )

                val response = apiClient.createWordDetails(
                    wordId = wordId,
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.CREATED
                response.body shouldNotBe null
                response.body!!.grammar shouldNotBe null
                response.body.grammar!!.comparativeForm shouldBe "more beautiful"
                response.body.grammar!!.superlativeForm shouldBe "most beautiful"
                response.body.pronunciation shouldNotBe null
                response.body.pronunciation!!.ipa shouldBe "ˈbjuːtɪfl"
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should return 401 for anonymous users`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id)

                val request = CreateWordDetailsRequest(
                    useCases = setOf("test"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf()
                )

                val response = apiClient.createWordDetails(
                    wordId = wordId,
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - should fail when word does not exist`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val nonExistentWordId = UUID.randomUUID()

                val request = CreateWordDetailsRequest(
                    useCases = setOf("test"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf()
                )

                val response = apiClient.createWordDetails(
                    wordId = nonExistentWordId,
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - should fail when word belongs to another user`() {
                val user1 = mockAuthenticatedUser(
                    email = "user1@test.com",
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val user2 = mockAuthenticatedUser(
                    email = "user2@test.com",
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(user1.userInfo.id)

                val request = CreateWordDetailsRequest(
                    useCases = setOf("test"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf()
                )

                val response = apiClient.createWordDetails(
                    wordId = wordId,
                    body = request,
                    user = user2
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `409 - should fail when word details already exist`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id)

                val request = CreateWordDetailsRequest(
                    useCases = setOf("test"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf()
                )

                // Create word details first time
                apiClient.createWordDetails(
                    wordId = wordId,
                    body = request,
                    user = authenticatedUser
                )

                // Try to create again
                val response = apiClient.createWordDetails(
                    wordId = wordId,
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.CONFLICT
            }
        }
    }

    @Nested
    @DisplayName("[PATCH] /api/v1/words/{wordId}/details - update word details")
    inner class UpdateWordDetailsTests {

        @Nested
        @DisplayName("Positive")
        inner class Positive {
            @Test
            fun `200 - should update word details with partial fields`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "test")

                // Create initial word details
                val createRequest = CreateWordDetailsRequest(
                    useCases = setOf("old use case"),
                    synonyms = setOf("old synonym"),
                    antonyms = setOf("old antonym"),
                    commonMistakes = setOf("old mistake"),
                    exampleSentences = setOf(
                        ExampleSentence(
                            context = "old context",
                            sentence = "old sentence",
                            translation = "old translation"
                        )
                    ),
                    collocations = setOf(),
                    culturalNotes = "old notes",
                    learningTips = "old tips"
                )

                apiClient.createWordDetails(
                    wordId = wordId,
                    body = createRequest,
                    user = authenticatedUser
                )

                // Update only some fields
                val updateRequest = UpdateWordDetailsRequest(
                    useCases = JsonNullable.of(setOf("new use case")),
                    synonyms = JsonNullable.of(setOf("new synonym", "another synonym")),
                    antonyms = JsonNullable.undefined(),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.undefined(),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.undefined(),
                    grammar = JsonNullable.undefined(),
                    culturalNotes = JsonNullable.of("new cultural notes"),
                    learningTips = JsonNullable.undefined()
                )

                val response = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.useCases shouldBe setOf("new use case")
                response.body.synonyms shouldBe setOf("new synonym", "another synonym")
                response.body.antonyms shouldBe setOf("old antonym") // not updated
                response.body.commonMistakes shouldBe setOf("old mistake") // not updated
                response.body.culturalNotes shouldBe "new cultural notes"
                response.body.learningTips shouldBe "old tips" // not updated

                // Verify in database
                val updated = wordDetailsRepository.findByWordIdAndUserId(
                    wordId = wordId,
                    userId = authenticatedUser.userInfo.id
                ).block()

                updated shouldNotBe null
            }

            @Test
            fun `200 - should update all fields`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "comprehensive")

                // Create initial word details
                val createRequest = CreateWordDetailsRequest(
                    useCases = setOf("old"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf()
                )

                apiClient.createWordDetails(
                    wordId = wordId,
                    body = createRequest,
                    user = authenticatedUser
                )

                // Update all fields
                val updateRequest = UpdateWordDetailsRequest(
                    useCases = JsonNullable.of(setOf("new use")),
                    synonyms = JsonNullable.of(setOf("thorough", "complete")),
                    antonyms = JsonNullable.of(setOf("incomplete")),
                    commonMistakes = JsonNullable.of(setOf("Don't confuse with comprehensible")),
                    exampleSentences = JsonNullable.of(
                        setOf(
                            ExampleSentence(
                                context = "Report writing",
                                sentence = "The report is comprehensive",
                                translation = "El informe es completo"
                            )
                        )
                    ),
                    collocations = JsonNullable.of(
                        setOf(
                            WordCollocation(
                                phrase = "comprehensive study",
                                translation = "estudio completo",
                                frequency = WordCollocationFrequency.COMMON
                            )
                        )
                    ),
                    pronunciation = JsonNullable.of(
                        WordPronunciation(
                            ipa = "ˌkɒmprɪˈhensɪv",
                            syllables = "com-pre-hen-sive",
                            stress = 3
                        )
                    ),
                    grammar = JsonNullable.of(
                        WordGrammar(
                            gender = null,
                            comparativeForm = "more comprehensive",
                            superlativeForm = "most comprehensive"
                        )
                    ),
                    culturalNotes = JsonNullable.of("Used in academic contexts"),
                    learningTips = JsonNullable.of("Related to comprehend")
                )

                val response = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.useCases shouldBe setOf("new use")
                response.body.synonyms shouldBe setOf("thorough", "complete")
                response.body.antonyms shouldBe setOf("incomplete")
                response.body.exampleSentences.size shouldBe 1
                response.body.collocations.size shouldBe 1
                response.body.pronunciation shouldNotBe null
                response.body.grammar shouldNotBe null
                response.body.culturalNotes shouldBe "Used in academic contexts"
                response.body.learningTips shouldBe "Related to comprehend"
            }

            @Test
            fun `200 - should clear nullable fields when set to null in Optional`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "clear")

                // Create with cultural notes and learning tips
                val createRequest = CreateWordDetailsRequest(
                    useCases = setOf("test"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf(),
                    culturalNotes = "Some notes",
                    learningTips = "Some tips"
                )

                apiClient.createWordDetails(
                    wordId = wordId,
                    body = createRequest,
                    user = authenticatedUser
                )

                // Clear the notes and tips
                val updateRequest = UpdateWordDetailsRequest(
                    useCases = JsonNullable.undefined(),
                    synonyms = JsonNullable.undefined(),
                    antonyms = JsonNullable.undefined(),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.undefined(),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.undefined(),
                    grammar = JsonNullable.undefined(),
                    culturalNotes = JsonNullable.of(null),
                    learningTips = JsonNullable.of(null)
                )

                val response = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.culturalNotes shouldBe null
                response.body.learningTips shouldBe null
            }

            @Test
            fun `200 - should update only example sentences`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "update")

                // Create initial word details
                val createRequest = CreateWordDetailsRequest(
                    useCases = setOf("verb"),
                    synonyms = setOf("modify"),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(
                        ExampleSentence(
                            context = "Software",
                            sentence = "Update the application",
                            translation = "Actualizar la aplicación"
                        )
                    ),
                    collocations = setOf()
                )

                apiClient.createWordDetails(
                    wordId = wordId,
                    body = createRequest,
                    user = authenticatedUser
                )

                // Update only example sentences
                val updateRequest = UpdateWordDetailsRequest(
                    useCases = JsonNullable.undefined(),
                    synonyms = JsonNullable.undefined(),
                    antonyms = JsonNullable.undefined(),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.of(
                        setOf(
                            ExampleSentence(
                                context = "Software",
                                sentence = "Update the system",
                                translation = "Actualizar el sistema"
                            ),
                            ExampleSentence(
                                context = "General",
                                sentence = "I need to update my records",
                                translation = "Necesito actualizar mis registros"
                            )
                        )
                    ),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.undefined(),
                    grammar = JsonNullable.undefined(),
                    culturalNotes = JsonNullable.undefined(),
                    learningTips = JsonNullable.undefined()
                )

                val response = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.exampleSentences.size shouldBe 2
                response.body.synonyms shouldBe setOf("modify") // unchanged
                response.body.useCases shouldBe setOf("verb") // unchanged
            }

            @Test
            fun `200 - should update pronunciation and grammar separately`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "read")

                // Create initial word details without pronunciation/grammar
                val createRequest = CreateWordDetailsRequest(
                    useCases = setOf("verb"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf(),
                    pronunciation = null,
                    grammar = null
                )

                apiClient.createWordDetails(
                    wordId = wordId,
                    body = createRequest,
                    user = authenticatedUser
                )

                // Add pronunciation
                val updateRequest1 = UpdateWordDetailsRequest(
                    useCases = JsonNullable.undefined(),
                    synonyms = JsonNullable.undefined(),
                    antonyms = JsonNullable.undefined(),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.undefined(),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.of(
                        WordPronunciation(
                            ipa = "riːd",
                            syllables = "read",
                            stress = 1
                        )
                    ),
                    grammar = JsonNullable.undefined(),
                    culturalNotes = JsonNullable.undefined(),
                    learningTips = JsonNullable.undefined()
                )

                val response1 = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = updateRequest1,
                    user = authenticatedUser
                )

                response1.status shouldBe HttpStatus.OK
                response1.body!!.pronunciation shouldNotBe null
                response1.body.pronunciation!!.ipa shouldBe "riːd"
                response1.body.grammar shouldBe null

                // Add grammar
                val updateRequest2 = UpdateWordDetailsRequest(
                    useCases = JsonNullable.undefined(),
                    synonyms = JsonNullable.undefined(),
                    antonyms = JsonNullable.undefined(),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.undefined(),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.undefined(),
                    grammar = JsonNullable.of(
                        WordGrammar(
                            gender = null,
                            irregularForms = mapOf(
                                "pastTense" to "read",
                                "pastParticiple" to "read"
                            )
                        )
                    ),
                    culturalNotes = JsonNullable.undefined(),
                    learningTips = JsonNullable.undefined()
                )

                val response2 = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = updateRequest2,
                    user = authenticatedUser
                )

                response2.status shouldBe HttpStatus.OK
                response2.body!!.pronunciation shouldNotBe null // still there
                response2.body.pronunciation!!.ipa shouldBe "riːd"
                response2.body.grammar shouldNotBe null
                response2.body.grammar!!.irregularForms shouldBe mapOf(
                    "pastTense" to "read",
                    "pastParticiple" to "read"
                )
            }

            @Test
            fun `200 - should replace sets completely when updated`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "replace")

                // Create with some synonyms
                val createRequest = CreateWordDetailsRequest(
                    useCases = setOf("verb"),
                    synonyms = setOf("substitute", "change"),
                    antonyms = setOf("keep", "maintain"),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf()
                )

                apiClient.createWordDetails(
                    wordId = wordId,
                    body = createRequest,
                    user = authenticatedUser
                )

                // Replace synonyms completely (not append)
                val updateRequest = UpdateWordDetailsRequest(
                    useCases = JsonNullable.undefined(),
                    synonyms = JsonNullable.of(setOf("swap")),
                    antonyms = JsonNullable.of(setOf()),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.undefined(),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.undefined(),
                    grammar = JsonNullable.undefined(),
                    culturalNotes = JsonNullable.undefined(),
                    learningTips = JsonNullable.undefined()
                )

                val response = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = updateRequest,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.synonyms shouldBe setOf("swap") // completely replaced
                response.body.antonyms shouldBe setOf() // cleared
                response.body.useCases shouldBe setOf("verb") // unchanged
            }

            @Test
            fun `200 - should handle multiple consecutive updates`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id, "evolve")

                // Create initial
                apiClient.createWordDetails(
                    wordId = wordId,
                    body = CreateWordDetailsRequest(
                        useCases = setOf("verb"),
                        synonyms = setOf(),
                        antonyms = setOf(),
                        commonMistakes = setOf(),
                        exampleSentences = setOf(),
                        collocations = setOf()
                    ),
                    user = authenticatedUser
                )

                // First update
                apiClient.updateWordDetails(
                    wordId = wordId,
                    body = UpdateWordDetailsRequest(
                        useCases = JsonNullable.of(setOf("verb", "develop")),
                        synonyms = JsonNullable.of(setOf("develop")),
                        antonyms = JsonNullable.undefined(),
                        commonMistakes = JsonNullable.undefined(),
                        exampleSentences = JsonNullable.undefined(),
                        collocations = JsonNullable.undefined(),
                        pronunciation = JsonNullable.undefined(),
                        grammar = JsonNullable.undefined(),
                        culturalNotes = JsonNullable.undefined(),
                        learningTips = JsonNullable.undefined()
                    ),
                    user = authenticatedUser
                )

                // Second update
                apiClient.updateWordDetails(
                    wordId = wordId,
                    body = UpdateWordDetailsRequest(
                        useCases = JsonNullable.undefined(),
                        synonyms = JsonNullable.of(setOf("progress", "advance")),
                        antonyms = JsonNullable.undefined(),
                        commonMistakes = JsonNullable.undefined(),
                        exampleSentences = JsonNullable.undefined(),
                        collocations = JsonNullable.undefined(),
                        pronunciation = JsonNullable.undefined(),
                        grammar = JsonNullable.undefined(),
                        culturalNotes = JsonNullable.of("Evolution is a gradual process"),
                        learningTips = JsonNullable.undefined()
                    ),
                    user = authenticatedUser
                )

                // Third update and verify
                val response = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = UpdateWordDetailsRequest(
                        useCases = JsonNullable.of(setOf("biology", "general")),
                        synonyms = JsonNullable.undefined(),
                        antonyms = JsonNullable.undefined(),
                        commonMistakes = JsonNullable.undefined(),
                        exampleSentences = JsonNullable.undefined(),
                        collocations = JsonNullable.undefined(),
                        pronunciation = JsonNullable.undefined(),
                        grammar = JsonNullable.undefined(),
                        culturalNotes = JsonNullable.undefined(),
                        learningTips = JsonNullable.of("Related to evolution")
                    ),
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.useCases shouldBe setOf("biology", "general")
                response.body.synonyms shouldBe setOf("progress", "advance")
                response.body.culturalNotes shouldBe "Evolution is a gradual process"
                response.body.learningTips shouldBe "Related to evolution"
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {
            @Test
            fun `401 - should return 401 for anonymous users`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id)

                val request = UpdateWordDetailsRequest(
                    useCases = JsonNullable.of(setOf("test")),
                    synonyms = JsonNullable.undefined(),
                    antonyms = JsonNullable.undefined(),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.undefined(),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.undefined(),
                    grammar = JsonNullable.undefined(),
                    culturalNotes = JsonNullable.undefined(),
                    learningTips = JsonNullable.undefined()
                )

                val response = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `404 - should fail when word does not exist`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val nonExistentWordId = UUID.randomUUID()

                val request = UpdateWordDetailsRequest(
                    useCases = JsonNullable.of(setOf("test")),
                    synonyms = JsonNullable.undefined(),
                    antonyms = JsonNullable.undefined(),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.undefined(),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.undefined(),
                    grammar = JsonNullable.undefined(),
                    culturalNotes = JsonNullable.undefined(),
                    learningTips = JsonNullable.undefined()
                )

                val response = apiClient.updateWordDetails(
                    wordId = nonExistentWordId,
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - should fail when word details do not exist`() {
                val authenticatedUser = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(authenticatedUser.userInfo.id)

                val request = UpdateWordDetailsRequest(
                    useCases = JsonNullable.of(setOf("test")),
                    synonyms = JsonNullable.undefined(),
                    antonyms = JsonNullable.undefined(),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.undefined(),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.undefined(),
                    grammar = JsonNullable.undefined(),
                    culturalNotes = JsonNullable.undefined(),
                    learningTips = JsonNullable.undefined()
                )

                val response = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }

            @Test
            fun `404 - should fail when word belongs to another user`() {
                val user1 = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val user2 = mockAuthenticatedUser(
                    languages = mapOf(LanguageName.ENGLISH to LanguageProficiencyLevel.C1)
                )
                val wordId = createTestWord(user1.userInfo.id)

                // Create word details for user1
                val createRequest = CreateWordDetailsRequest(
                    useCases = setOf("test"),
                    synonyms = setOf(),
                    antonyms = setOf(),
                    commonMistakes = setOf(),
                    exampleSentences = setOf(),
                    collocations = setOf()
                )

                apiClient.createWordDetails(
                    wordId = wordId,
                    body = createRequest,
                    user = user1
                )

                // Try to update as user2
                val updateRequest = UpdateWordDetailsRequest(
                    useCases = JsonNullable.of(setOf("malicious")),
                    synonyms = JsonNullable.undefined(),
                    antonyms = JsonNullable.undefined(),
                    commonMistakes = JsonNullable.undefined(),
                    exampleSentences = JsonNullable.undefined(),
                    collocations = JsonNullable.undefined(),
                    pronunciation = JsonNullable.undefined(),
                    grammar = JsonNullable.undefined(),
                    culturalNotes = JsonNullable.undefined(),
                    learningTips = JsonNullable.undefined()
                )

                val response = apiClient.updateWordDetails(
                    wordId = wordId,
                    body = updateRequest,
                    user = user2
                )

                response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }
}