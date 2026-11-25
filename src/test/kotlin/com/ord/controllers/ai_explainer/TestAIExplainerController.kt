package com.ord.controllers.ai_explainer

import com.ord.config.properties.SessionProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.auth.repositories.OtpCodeRepository
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.security.UserRepository
import com.ord.core.ai_provider_usage.models.AiProviderUsageOperationType
import com.ord.features.ai_explainer.api.requests.ExplainPhraseAutocompleteRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseFollowUpRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseEnhanceRequest
import com.ord.features.ai_explainer.api.requests.ExplainPhraseSuggestActionsRequest
import com.ord.features.ai_explainer.model.enums.ExplainPhraseFollowUpAction
import com.ord.features.ai_explainer.model.enums.ExplainPhraseKind
import com.ord.features.ai_explainer.models.explanation.AIExplanationEntity
import com.ord.features.ai_explainer.models.explanation.AIExplanationMapper
import com.ord.features.ai_explainer.repositories.AIExplanationRepository
import com.ord.testing_utils.api.clients.AIExplainerAPIClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000")
@DisplayName("- AIExplainerController")
class TestAIExplainerController @Autowired constructor(
    sessionProperties: SessionProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    webClient: WebTestClient,
    userRepository: UserRepository,
    otpCodeRepository: OtpCodeRepository,
    passwordEncoder: PasswordEncoder,
    aiProviderUsageRepository: AiProviderUsageRepository,
    private val aiExplanationRepository: AIExplanationRepository,
    private val aiExplanationMapper: AIExplanationMapper
) : ControllerTestBase(
    webClient = webClient,
    sessionProperties = sessionProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userRepository = userRepository,
    otpCodeRepository = otpCodeRepository,
    passwordEncoder = passwordEncoder,
    aiProviderUsageRepository = aiProviderUsageRepository
) {
    private val aiExplainerAPIClient = AIExplainerAPIClient(webClient)

    lateinit var authenticatedUser: MockedAuthenticatedUser

    @BeforeEach
    fun beforeEach() {
        authenticatedUser = mockAuthenticatedUser(
            languages = mapOf(
                LanguageName.NORWEGIAN to LanguageProficiencyLevel.B2,
                LanguageName.ENGLISH to LanguageProficiencyLevel.C2
            )
        )
    }

    @Nested
    @DisplayName("[POST] /api/v1/ai-explainer/explain-phrase - explain word or phrase")
    inner class ExplainPhrase {

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            fun `200 - should generate explanation for a simple word`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()

                assertAiProviderUsageLogCreated(authenticatedUser.userInfo.id, "AI_EXPLAINER_EXPLAIN_PHRASE")
            }

            @Test
            fun `200 - should generate explanation for a phrase`() {
                val request = ExplainPhraseRequest(
                    phrase = "break the ice",
                    language = LanguageName.ENGLISH
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
            }

            @Test
            fun `200 - should generate explanation for complex word`() {
                val request = ExplainPhraseRequest(
                    phrase = "hygge",
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
            }

            @Test
            fun `200 - should generate explanation with additional context`() {
                val request = ExplainPhraseRequest(
                    phrase = "break the ice",
                    language = LanguageName.ENGLISH,
                    context = "I was reading a book about networking events. The author mentioned that at parties, people often need to 'break the ice' to start conversations with strangers."
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()

                assertAiProviderUsageLogCreated(authenticatedUser.userInfo.id, "AI_EXPLAINER_EXPLAIN_PHRASE")
            }

            @Test
            fun `200 - should generate explanation with custom instruction in English`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    customInstruction = "Please explain this word as if I'm 5 years old"
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()

                assertAiProviderUsageLogCreated(authenticatedUser.userInfo.id, "AI_EXPLAINER_EXPLAIN_PHRASE")
            }

            @Test
            fun `200 - should generate explanation with custom instruction in another language`() {
                val request = ExplainPhraseRequest(
                    phrase = "katt",
                    language = LanguageName.NORWEGIAN,
                    customInstruction = "Concentrer sur l'usage familier et informel"
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
            }

            @Test
            fun `200 - should generate explanation with both context and custom instruction`() {
                val request = ExplainPhraseRequest(
                    phrase = "over the moon",
                    language = LanguageName.ENGLISH,
                    context = "She was over the moon when she got the job offer.",
                    customInstruction = "Focus on the emotional aspect and provide similar expressions"
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - anonymous user cannot get explanation`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = null
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - empty phrase should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "",
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - extremely long phrase should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "a".repeat(256),
                    language = LanguageName.NORWEGIAN
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - user without proficiency in language should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "hola",
                    language = LanguageName.SPANISH
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - context exceeding max length should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    context = "a".repeat(2001)
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - custom instruction exceeding max length should fail`() {
                val request = ExplainPhraseRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    customInstruction = "a".repeat(501)
                )

                val response = aiExplainerAPIClient.explainPhrase(
                    body = request,
                    user = authenticatedUser
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/ai-explainer/explain-phrase/follow-up - follow up on explanation")
    inner class FollowUpExplainPhrase {

        private val samplePreviousExplanation =
            "The word \"hund\" means \"dog\" in Norwegian. It is a common noun used for pets and animals."

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            fun `200 - should generate simpler follow-up`() {
                val request = ExplainPhraseFollowUpRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    previousExplanation = samplePreviousExplanation,
                    action = ExplainPhraseFollowUpAction.SIMPLER,
                )

                val response = aiExplainerAPIClient.followUpExplainPhrase(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
                assertAiProviderUsageLogCreated(
                    authenticatedUser.userInfo.id,
                    AiProviderUsageOperationType.AIExplainer.FollowUp.SIMPLER,
                )
            }

            @Test
            fun `200 - should generate more examples follow-up`() {
                val request = ExplainPhraseFollowUpRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    previousExplanation = samplePreviousExplanation,
                    action = ExplainPhraseFollowUpAction.MORE_EXAMPLES,
                )

                val response = aiExplainerAPIClient.followUpExplainPhrase(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
                assertAiProviderUsageLogCreated(
                    authenticatedUser.userInfo.id,
                    AiProviderUsageOperationType.AIExplainer.FollowUp.MORE_EXAMPLES,
                )
            }

            @Test
            fun `200 - should generate similar expressions follow-up`() {
                val request = ExplainPhraseFollowUpRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    previousExplanation = samplePreviousExplanation,
                    action = ExplainPhraseFollowUpAction.SIMILAR_EXPRESSIONS,
                )

                val response = aiExplainerAPIClient.followUpExplainPhrase(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.OK
                response.explanation.shouldNotBeBlank()
                assertAiProviderUsageLogCreated(
                    authenticatedUser.userInfo.id,
                    AiProviderUsageOperationType.AIExplainer.FollowUp.SIMILAR_EXPRESSIONS,
                )
            }

        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - anonymous user cannot get follow-up`() {
                val request = ExplainPhraseFollowUpRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    previousExplanation = samplePreviousExplanation,
                    action = ExplainPhraseFollowUpAction.SIMPLER,
                )

                val response = aiExplainerAPIClient.followUpExplainPhrase(
                    body = request,
                    user = null,
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - empty previous explanation should fail`() {
                val request = ExplainPhraseFollowUpRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    previousExplanation = "",
                    action = ExplainPhraseFollowUpAction.SIMPLER,
                )

                val response = aiExplainerAPIClient.followUpExplainPhrase(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - previous explanation exceeding max length should fail`() {
                val request = ExplainPhraseFollowUpRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    previousExplanation = "a".repeat(4001),
                    action = ExplainPhraseFollowUpAction.SIMPLER,
                )

                val response = aiExplainerAPIClient.followUpExplainPhrase(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - user without proficiency in language should fail`() {
                val request = ExplainPhraseFollowUpRequest(
                    phrase = "hola",
                    language = LanguageName.SPANISH,
                    previousExplanation = samplePreviousExplanation,
                    action = ExplainPhraseFollowUpAction.SIMPLER,
                )

                val response = aiExplainerAPIClient.followUpExplainPhrase(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - context exceeding max length should fail`() {
                val request = ExplainPhraseFollowUpRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    previousExplanation = samplePreviousExplanation,
                    action = ExplainPhraseFollowUpAction.SIMPLER,
                    context = "a".repeat(2001),
                )

                val response = aiExplainerAPIClient.followUpExplainPhrase(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/ai-explainer/autocomplete - tab completion for the explain input")
    inner class AutocompletePhrase {

        @Nested
        @DisplayName("Positive")
        inner class Positive {

            @Test
            fun `200 - should return a completion without logging AI usage`() {
                val request = ExplainPhraseAutocompleteRequest(
                    prefix = "break the",
                    language = LanguageName.ENGLISH,
                )

                val response = aiExplainerAPIClient.autocompletePhrase(
                    body = request,
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.OK
                response.body shouldNotBe null
                response.body!!.completion.shouldNotBeBlank()
                assertAiProviderUsageLogNotCreated(
                    authenticatedUser.userInfo.id,
                    AiProviderUsageOperationType.AIExplainer.AUTOCOMPLETE,
                )
            }
        }

        @Nested
        @DisplayName("Negative")
        inner class Negative {

            @Test
            fun `401 - anonymous user cannot get a completion`() {
                val response = aiExplainerAPIClient.autocompletePhrase(
                    body = ExplainPhraseAutocompleteRequest(prefix = "break the", language = LanguageName.ENGLISH),
                    user = null,
                )

                response.status shouldBe HttpStatus.UNAUTHORIZED
            }

            @Test
            fun `400 - blank prefix should fail`() {
                val response = aiExplainerAPIClient.autocompletePhrase(
                    body = ExplainPhraseAutocompleteRequest(prefix = "  ", language = LanguageName.ENGLISH),
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - prefix exceeding max length should fail`() {
                val response = aiExplainerAPIClient.autocompletePhrase(
                    body = ExplainPhraseAutocompleteRequest(prefix = "a".repeat(256), language = LanguageName.ENGLISH),
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }

            @Test
            fun `400 - user without proficiency in language should fail`() {
                val response = aiExplainerAPIClient.autocompletePhrase(
                    body = ExplainPhraseAutocompleteRequest(prefix = "hol", language = LanguageName.SPANISH),
                    user = authenticatedUser,
                )

                response.status shouldBe HttpStatus.BAD_REQUEST
            }
        }
    }

    @Nested
    @DisplayName("[POST] /api/v1/ai-explainer/explain-phrase/enhance - rewrite the typed request")
    inner class EnhancePrompt {

        @Test
        fun `200 - should return a non-blank enhanced request within the length limit`() {
            val response = aiExplainerAPIClient.enhancePrompt(
                body = ExplainPhraseEnhanceRequest(prompt = "tell me more about a word contraction", language = LanguageName.ENGLISH),
                user = authenticatedUser
            )

            response.status shouldBe HttpStatus.OK
            response.body!!.prompt.shouldNotBeBlank()
            response.body!!.prompt.length shouldBeLessThanOrEqual 255

            assertAiProviderUsageLogCreated(authenticatedUser.userInfo.id, "AI_EXPLAINER_ENHANCE_PROMPT")
        }

        @Test
        fun `400 - blank prompt should fail`() {
            aiExplainerAPIClient.enhancePrompt(
                body = ExplainPhraseEnhanceRequest(prompt = "  ", language = LanguageName.ENGLISH),
                user = authenticatedUser
            ).status shouldBe HttpStatus.BAD_REQUEST
        }

        @Test
        fun `400 - prompt exceeding max length should fail`() {
            aiExplainerAPIClient.enhancePrompt(
                body = ExplainPhraseEnhanceRequest(prompt = "a".repeat(256), language = LanguageName.ENGLISH),
                user = authenticatedUser
            ).status shouldBe HttpStatus.BAD_REQUEST
        }

        @Test
        fun `400 - user without proficiency in language should fail`() {
            aiExplainerAPIClient.enhancePrompt(
                body = ExplainPhraseEnhanceRequest(prompt = "hola", language = LanguageName.SPANISH),
                user = authenticatedUser
            ).status shouldBe HttpStatus.BAD_REQUEST
        }

        @Test
        fun `401 - enhance without authentication should fail`() {
            aiExplainerAPIClient.enhancePrompt(
                body = ExplainPhraseEnhanceRequest(prompt = "hund", language = LanguageName.NORWEGIAN)
            ).status shouldBe HttpStatus.UNAUTHORIZED
        }
    }

    @Nested
    @DisplayName("Saved explanations - create on stream end, update on follow-up, delete on demand")
    inner class SavedExplanations {

        private fun findSavedNow(id: UUID) =
            aiExplanationRepository.findByIdAndUserId(id, authenticatedUser.userInfo.id).block()

        /**
         * The stubbed AI client ends the stream without waiting for the fire-and-forget write that follows it
         * (the real stream does wait), so tests poll until the expected state shows up.
         */
        private fun awaitSaved(
            id: UUID,
            condition: (AIExplanationEntity?) -> Boolean = { it != null }
        ): AIExplanationEntity? {
            var saved = findSavedNow(id)
            var attempts = 0

            while (!condition(saved) && attempts < 30) {
                Thread.sleep(100)
                saved = findSavedNow(id)
                attempts++
            }

            return saved
        }

        private fun findSaved(id: UUID) = awaitSaved(id)

        @Test
        fun `should save the explanation when the explain stream finishes`() {
            val id = UUID.randomUUID()

            val response = aiExplainerAPIClient.explainPhrase(
                body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN, explanationId = id),
                user = authenticatedUser
            )

            response.status shouldBe HttpStatus.OK
            val saved = findSaved(id)
            saved shouldNotBe null
            saved!!.phrase shouldBe "hund"
            saved.language shouldBe LanguageName.NORWEGIAN
            saved.explanation.shouldNotBeBlank()
        }

        @Test
        fun `should not save anything when explanationId is omitted`() {
            aiExplainerAPIClient.explainPhrase(
                body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN),
                user = authenticatedUser
            )

            aiExplanationRepository.findAllByUserId(authenticatedUser.userInfo.id).collectList().block()!!.size shouldBe 0
        }

        @Test
        fun `should store a follow-up on the existing entry`() {
            val id = UUID.randomUUID()
            val explanation = aiExplainerAPIClient.explainPhrase(
                body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN, explanationId = id),
                user = authenticatedUser
            ).explanation

            aiExplainerAPIClient.followUpExplainPhrase(
                body = ExplainPhraseFollowUpRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    previousExplanation = explanation,
                    action = ExplainPhraseFollowUpAction.SIMPLER,
                    explanationId = id
                ),
                user = authenticatedUser
            ).status shouldBe HttpStatus.OK

            val saved = awaitSaved(id) { entity ->
                entity != null && aiExplanationMapper.deserializeFollowUps(entity.followUps).isNotEmpty()
            }!!
            val followUps = aiExplanationMapper.deserializeFollowUps(saved.followUps)
            followUps.keys shouldBe setOf(ExplainPhraseFollowUpAction.SIMPLER)
            followUps.getValue(ExplainPhraseFollowUpAction.SIMPLER).shouldNotBeBlank()
        }

        @Test
        fun `should store new follow-up actions side by side under their own keys`() {
            val id = UUID.randomUUID()
            val explanation = aiExplainerAPIClient.explainPhrase(
                body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN, explanationId = id),
                user = authenticatedUser
            ).explanation

            listOf(ExplainPhraseFollowUpAction.COMMON_MISTAKES, ExplainPhraseFollowUpAction.USED_WITH).forEach {
                aiExplainerAPIClient.followUpExplainPhrase(
                    body = ExplainPhraseFollowUpRequest(
                        phrase = "hund",
                        language = LanguageName.NORWEGIAN,
                        previousExplanation = explanation,
                        action = it,
                        explanationId = id
                    ),
                    user = authenticatedUser
                ).status shouldBe HttpStatus.OK
            }

            aiExplanationMapper.deserializeFollowUps(
                awaitSaved(id) { entity ->
                    entity != null && aiExplanationMapper.deserializeFollowUps(entity.followUps).size == 2
                }!!.followUps
            ).keys shouldBe
                setOf(ExplainPhraseFollowUpAction.COMMON_MISTAKES, ExplainPhraseFollowUpAction.USED_WITH)
        }

        @Test
        fun `200 - suggest-actions should return ranked actions and save the kind`() {
            val id = UUID.randomUUID()
            val explanation = aiExplainerAPIClient.explainPhrase(
                body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN, explanationId = id),
                user = authenticatedUser
            ).explanation

            val response = aiExplainerAPIClient.suggestActions(
                body = ExplainPhraseSuggestActionsRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    explanation = explanation,
                    explanationId = id
                ),
                user = authenticatedUser
            )

            response.status shouldBe HttpStatus.OK
            response.body!!.kind shouldBe ExplainPhraseKind.WORD_OR_PHRASE
            response.body!!.actions.first() shouldBe ExplainPhraseFollowUpAction.MORE_EXAMPLES
            response.body!!.actions.toSet() shouldBe ExplainPhraseKind.WORD_OR_PHRASE.allowedActions.toSet()
            awaitSaved(id) { it?.kind != null }!!.kind shouldBe ExplainPhraseKind.WORD_OR_PHRASE
            response.body!!.emoji shouldBe "🐕"
            findSaved(id)!!.emoji shouldBe "🐕"
        }

        @Test
        fun `400 - suggest-actions without proficiency in the language should fail`() {
            aiExplainerAPIClient.suggestActions(
                body = ExplainPhraseSuggestActionsRequest(
                    phrase = "hola",
                    language = LanguageName.SPANISH,
                    explanation = "Hello."
                ),
                user = authenticatedUser
            ).status shouldBe HttpStatus.BAD_REQUEST
        }

        @Test
        fun `401 - suggest-actions without authentication should fail`() {
            aiExplainerAPIClient.suggestActions(
                body = ExplainPhraseSuggestActionsRequest(
                    phrase = "hund",
                    language = LanguageName.NORWEGIAN,
                    explanation = "A dog."
                )
            ).status shouldBe HttpStatus.UNAUTHORIZED
        }

        @Test
        fun `200 - should list the user's saved explanations newest first and only theirs`() {
            val first = UUID.randomUUID()
            val second = UUID.randomUUID()
            listOf(first, second).forEach {
                aiExplainerAPIClient.explainPhrase(
                    body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN, explanationId = it),
                    user = authenticatedUser
                )
            }

            val listed = aiExplainerAPIClient.getExplanations(authenticatedUser)

            listed.status shouldBe HttpStatus.OK
            listed.body!!.map { it.id } shouldBe listOf(second, first)
            aiExplainerAPIClient.getExplanations(mockAuthenticatedUser()).body!!.size shouldBe 0
        }

        @Test
        fun `200 - should return one saved explanation by id`() {
            val id = UUID.randomUUID()
            aiExplainerAPIClient.explainPhrase(
                body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN, explanationId = id),
                user = authenticatedUser
            )
            awaitSaved(id)

            val response = aiExplainerAPIClient.getExplanation(id, authenticatedUser)

            response.status shouldBe HttpStatus.OK
            response.body!!.id shouldBe id
            response.body!!.phrase shouldBe "hund"
            response.body!!.explanation.shouldNotBeBlank()
        }

        @Test
        fun `404 - unknown or foreign explanation should not be returned`() {
            val id = UUID.randomUUID()
            aiExplainerAPIClient.explainPhrase(
                body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN, explanationId = id),
                user = authenticatedUser
            )
            awaitSaved(id)

            aiExplainerAPIClient.getExplanation(UUID.randomUUID(), authenticatedUser).status shouldBe HttpStatus.NOT_FOUND
            aiExplainerAPIClient.getExplanation(id, mockAuthenticatedUser()).status shouldBe HttpStatus.NOT_FOUND
        }

        @Test
        fun `401 - getting one explanation without authentication should fail`() {
            aiExplainerAPIClient.getExplanation(UUID.randomUUID()).status shouldBe HttpStatus.UNAUTHORIZED
        }

        @Test
        fun `401 - listing without authentication should fail`() {
            aiExplainerAPIClient.getExplanations().status shouldBe HttpStatus.UNAUTHORIZED
        }

        @Test
        fun `204 - should delete the saved explanation`() {
            val id = UUID.randomUUID()
            aiExplainerAPIClient.explainPhrase(
                body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN, explanationId = id),
                user = authenticatedUser
            )

            findSaved(id) shouldNotBe null // the write lands asynchronously; wait for it before deleting
            aiExplainerAPIClient.deleteExplanation(id, authenticatedUser).status shouldBe HttpStatus.NO_CONTENT

            findSavedNow(id) shouldBe null
        }

        @Test
        fun `204 - deleting a missing explanation should be a no-op`() {
            aiExplainerAPIClient.deleteExplanation(UUID.randomUUID(), authenticatedUser).status shouldBe HttpStatus.NO_CONTENT
        }

        @Test
        fun `should not delete another user's explanation`() {
            val id = UUID.randomUUID()
            aiExplainerAPIClient.explainPhrase(
                body = ExplainPhraseRequest(phrase = "hund", language = LanguageName.NORWEGIAN, explanationId = id),
                user = authenticatedUser
            )
            val otherUser = mockAuthenticatedUser()

            aiExplainerAPIClient.deleteExplanation(id, otherUser).status shouldBe HttpStatus.NO_CONTENT

            findSaved(id) shouldNotBe null
        }

        @Test
        fun `401 - delete without authentication should fail`() {
            aiExplainerAPIClient.deleteExplanation(UUID.randomUUID()).status shouldBe HttpStatus.UNAUTHORIZED
        }
    }
}
