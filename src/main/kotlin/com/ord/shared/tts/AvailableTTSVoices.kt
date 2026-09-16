package com.ord.shared.tts

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import java.math.BigDecimal

enum class AvailableTTSVoices(
    val language: LanguageName,
    val voiceId: String,
    val modelId: String = "eleven_turbo_v2_5",
    val pricePerThousandCharacters: BigDecimal,
) {
    ENGLISH(
        language = LanguageName.ENGLISH,
        voiceId = "TWutjvRaJqAX89preB4e",
        pricePerThousandCharacters = BigDecimal("0.15"),
    ),
    GERMAN(
        language = LanguageName.GERMAN,
        voiceId = "SiMvlSW9cKKHDYT4BzOp",
        pricePerThousandCharacters = BigDecimal("0.15"),
    ),
    SPANISH(
        language = LanguageName.SPANISH,
        voiceId = "eZxqQzb5CuYo3Kl6EXfZ",
        pricePerThousandCharacters = BigDecimal("0.15"),
    ),
    ;

    // Planned voices — uncomment when ElevenLabs voice IDs are ready:
    // FRENCH(LanguageName.FRENCH, "...", pricePerThousandCharacters = BigDecimal("0.15")),
    // ITALIAN(LanguageName.ITALIAN, "...", pricePerThousandCharacters = BigDecimal("0.15")),
    // POLISH(LanguageName.POLISH, "...", pricePerThousandCharacters = BigDecimal("0.15")),
    // NORWEGIAN(LanguageName.NORWEGIAN, "...", pricePerThousandCharacters = BigDecimal("0.15")),
    // RUSSIAN(LanguageName.RUSSIAN, "...", pricePerThousandCharacters = BigDecimal("0.15")),
    // SLOVENIAN(LanguageName.SLOVENIAN, "...", pricePerThousandCharacters = BigDecimal("0.15")),

    companion object {
        val DEFAULT = ENGLISH

        fun forLanguage(language: LanguageName): AvailableTTSVoices? =
            entries.find { it.language == language }
    }
}
