package com.ord.core.ai_provider.services

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.dto.OpenAIResponse
import com.ord.core.ai_provider.dto.helpers.StreamCompletedPayload
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model.enums.GamesGPTTokensConsumptionType
import com.ord.shared.prompts.Prompt
import reactor.core.publisher.Sinks

interface OpenAIAPIClientService {
    fun <T> makeRequest(
        aiResponseTypeReference: TypeReference<T>,

        prompt: String,

        saveLog: (openAIResponse: OpenAIResponse) -> Unit,
        validateResponseBody: (parsedResponseBody: T?) -> Boolean,
        parseResponseBody: (responseBody: T) -> T = { it }
    ): T

    fun openStream(
        prompt: String,
        onChunkReceived: (String) -> Unit,
        onError: (Throwable) -> Unit = { throw it },
        onComplete: (StreamCompletedPayload) -> Unit,
    ): Sinks.Many<String>
}
