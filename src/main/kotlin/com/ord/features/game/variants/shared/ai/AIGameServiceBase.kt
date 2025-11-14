package com.ord.features.game.variants.shared.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.services.GptTokensUsageService
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import java.util.*

abstract class AIGameServiceBase {
    @Autowired
    protected lateinit var openAIAPIClientService: OpenAIAPIClientService

    @Autowired
    protected lateinit var gptTokensUsageService: GptTokensUsageService

    private val logger = LoggerFactory.getLogger(AIGameServiceBase::class.java)

    fun <T> makeGameAIRequest(
        userId: UUID,
        operationType: String,
        aiResponseTypeReference: TypeReference<T>,
        prompt: String,
        parseResponseBody: (T) -> T = { it },
        validateResponseBody: (T?) -> Boolean
    ): Mono<T> {
        return openAIAPIClientService.makeRequest(
            prompt = prompt,
            userId = userId,
            gptTokensUsageLogKey = operationType,
            validateResponseBody = validateResponseBody,
            parseResponseBody = parseResponseBody,
            aiResponseType = aiResponseTypeReference
        )
    }
}