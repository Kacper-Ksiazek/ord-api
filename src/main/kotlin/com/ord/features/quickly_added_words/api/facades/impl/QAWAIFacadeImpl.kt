package com.ord.features.quickly_added_words.api.facades.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.models.GptTokensUsageOperationType
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserDTO
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
import com.ord.exceptions.REST.BadRequestException
import com.ord.exceptions.REST.InternalServerError
import com.ord.features.quickly_added_words.api.ai.responses.openai.OpenAIQAWFillGapsBatch
import com.ord.features.quickly_added_words.api.facades.QAWAIFacade
import com.ord.features.quickly_added_words.api.requests.QAWFillGapsRequest
import com.ord.features.quickly_added_words.api.responses.QAWFillGapsResponse
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import com.ord.shared.utils.EnumUtils.joinEnumValues
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class QAWAIFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
) : QAWAIFacade {

    override fun fillGaps(
        body: QAWFillGapsRequest,
        user: UserDTO,
    ): Mono<QAWFillGapsResponse> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMap { userProficiencyInRequestedLanguage ->
                val wordsList = body.items
                    .mapIndexed { index, item -> "${index + 1}. ${item.word}" }
                    .joinToString(separator = "\n")

                val prompt = Prompt(
                    variant = AvailablePrompts.QAW_FILL_GAPS,
                    params = mapOf(
                        "words" to wordsList,
                        "wordCount" to body.items.size.toString(),
                        "wordLanguage" to body.language.toString(),
                        "desiredLanguage" to userProficiencyInRequestedLanguage!!.translateTo.toString(),
                        "proficiency" to userProficiencyInRequestedLanguage.level.toString(),
                        "generativeContentLanguage" to userProficiencyInRequestedLanguage.generativeContentLanguage.toString(),
                        "wordTypes" to WordType::class.joinEnumValues(separator = " | "),
                        "wordExtraMarks" to WordExtraMark::class.joinEnumValues(separator = " | "),
                    ),
                )

                openAIAPIClientService
                    .makeRequest(
                        aiResponseType = object : TypeReference<OpenAIQAWFillGapsBatch>() {},
                        prompt = prompt,
                        userId = user.id,
                        gptTokensUsageLogKey = GptTokensUsageOperationType.QAW.FILL_GAPS,
                    )
                    .map { batch ->
                        if (batch.items.size != body.items.size) {
                            throw InternalServerError(
                                "AI returned ${batch.items.size} items but ${body.items.size} were requested.",
                            )
                        }
                        batch.toDomain()
                    }
            }
    }
}
