package com.ord.core.ai_provider_usage.services

import com.ord.shared.prompts.AvailableAIModels
import com.ord.shared.tts.AvailableTTSVoices
import java.math.BigDecimal
import java.math.RoundingMode

object AiUsagePriceCalculator {
    private val MILLION = BigDecimal("1000000")
    private val THOUSAND = BigDecimal("1000")
    private const val PRICE_SCALE = 6

    fun forOpenAi(modelId: String, inputTokens: Int, outputTokens: Int): BigDecimal {
        val model = AvailableAIModels.entries.find { it.model == modelId } ?: AvailableAIModels.DEFAULT

        val inputPrice = model.pricePerMlnInputTokens
            .multiply(inputTokens.toBigDecimal())
            .divide(MILLION, PRICE_SCALE, RoundingMode.HALF_UP)

        val outputPrice = model.pricePerMlnOutputTokens
            .multiply(outputTokens.toBigDecimal())
            .divide(MILLION, PRICE_SCALE, RoundingMode.HALF_UP)

        return inputPrice.add(outputPrice)
    }

    fun forElevenLabs(voiceId: String, characterCount: Int): BigDecimal {
        val voice = AvailableTTSVoices.entries.find { it.voiceId == voiceId }
            ?: AvailableTTSVoices.DEFAULT

        return voice.pricePerThousandCharacters
            .multiply(characterCount.toBigDecimal())
            .divide(THOUSAND, PRICE_SCALE, RoundingMode.HALF_UP)
    }
}
