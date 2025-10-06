package com.ord.features.game.variants.shared.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

abstract class AIGameServiceBase {
    @Autowired
    protected lateinit var openAIAPIClientService: OpenAIAPIClientService

    fun <T> makeGameAIRequest(
        aiResponseTypeReference: TypeReference<T>,
        prompt: String,
        parseResponseBody: (T) -> T = { it },
        validateResponseBody: (T?) -> Boolean
    ): Mono<T> {
        return openAIAPIClientService.makeRequest(
            prompt = prompt,
            validateResponseBody = validateResponseBody,
            parseResponseBody = parseResponseBody,
            aiResponseType = aiResponseTypeReference,
            saveLog = {
                // TODO: Implement this in the future
            },
        )
    }
}