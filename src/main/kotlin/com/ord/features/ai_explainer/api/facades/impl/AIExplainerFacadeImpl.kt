package com.ord.features.ai_explainer.api.facades.impl

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserDTO
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.ai_explainer.api.facades.AIExplainerFacade
import com.ord.features.ai_explainer.api.requests.ExplainPhraseRequest
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class AIExplainerFacadeImpl(
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
) : AIExplainerFacade {

    override fun explainPhrase(
        body: ExplainPhraseRequest,
        user: UserDTO,
    ): Flux<String> {
        return languageProficiencyService.findUserProficiencyInLanguage(user.id, body.language)
            .switchIfEmpty(Mono.error(BadRequestException("User does not have any proficiency in the requested language.")))
            .flatMapMany { userProficiencyInRequestedLanguage ->
                val translateTo: LanguageName = userProficiencyInRequestedLanguage!!.translateTo
                val proficiencyLevel: LanguageProficiencyLevel = userProficiencyInRequestedLanguage.level

                val prompt = Prompt(
                    variant = AvailablePrompts.WORDS_EXPLAIN,
                    params = mapOf(
                        "word" to body.phrase,
                        "wordLanguage" to body.language.toString(),
                        "translationLanguage" to translateTo.toString(),
                        "proficiency" to proficiencyLevel.toString(),
                        "generativeContentLanguage" to userProficiencyInRequestedLanguage.generativeContentLanguage.toString(),
                        "additionalContext" to (body.context ?: "Not provided"),
                        "customInstruction" to (body.customInstruction ?: "Not provided")
                    )
                ).toString()

                openAIAPIClientService.openSimpleStringStream(
                    prompt = prompt,
                    onComplete = { (_, emitter) ->
                        emitter.tryEmitComplete()
                    }
                )
            }
    }
}