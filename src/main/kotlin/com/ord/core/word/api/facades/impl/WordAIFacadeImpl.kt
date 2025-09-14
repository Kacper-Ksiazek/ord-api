package com.ord.core.word.api.facades.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.facades.WordAIFacade
import com.ord.core.word.api.requests.dto.GenerateWordManualRequest
import com.ord.core.word.api.responses.dto.AIGeneratedWordManual
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.exceptions.REST.BadRequestException
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.utils.EnumUtils.joinEnumValues
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class WordAIFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
) : WordAIFacade {
    private val jsonObjectMapper: ObjectMapper = jacksonObjectMapper()

    override fun generateWordManual(
        body: GenerateWordManualRequest,
        user: UserEntity
    ): Mono<AIGeneratedWordManual> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMap { userProficiencyInRequestedLanguage ->
                val translateTo: LanguageName =
                    body.targetLanguage ?: userProficiencyInRequestedLanguage!!.generativeContentLanguage
                val proficiencyLevel: LanguageProficiencyLevel =
                    body.proficiencyLevel ?: userProficiencyInRequestedLanguage!!.level

                val prompt = Prompt(
                    variant = AvailablePrompts.WORDS_GENERATE_MANUAL,
                    params = mapOf(
                        "word" to body.word,
                        "wordLanguage" to body.language.toString(),
                        "desiredLanguage" to translateTo.toString(),
                        "proficiency" to proficiencyLevel.toString(),
                        "generativeContentLanguage" to userProficiencyInRequestedLanguage!!.generativeContentLanguage.toString(),

                        "wordTypes" to WordType::class.joinEnumValues(separator = " | "),
                        "wordExtraMarks" to WordExtraMark::class.joinEnumValues(separator = " | ")
                    )
                ).toString()

                // Send the request to OpenAI using reactive service
                openAIAPIClientService.makeRequest(
                    aiResponseType = object : TypeReference<AIGeneratedWordManual>() {},
                    prompt = prompt,
                    saveLog = { /* Log handling can be added here if needed */ },
                    validateResponseBody = { responseBody ->
                        responseBody != null &&
                                !responseBody.toString().contains("WORD_MISSPELLED") &&
                                !responseBody.toString().contains("NON_EXISTENT_WORD")
                    },
                    parseResponseBody = { responseBody ->
                        when {
                            responseBody.toString().contains("WORD_MISSPELLED") ->
                                throw BadRequestException("The word ${body.word} in the language ${body.language} is misspelled.")

                            responseBody.toString().contains("NON_EXISTENT_WORD") ->
                                throw BadRequestException("The word ${body.word} does not exist in the language ${body.language}.")

                            else -> {
                                responseBody.originalWord = body.word
                                responseBody
                            }
                        }
                    }
                )
            }
    }
}