package com.ord.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "elevenlabs")
class ElevenLabsProperties(
    var apiKey: String = "",
    var apiUrl: String = "",
    var voiceId: String = "DODLEQrClDo8wCz460ld",
    var modelId: String = "eleven_turbo_v2_5",
    var outputFormat: String = "mp3_44100_128",
    var optimizeStreamingLatency: Int = 3,

    var connectTimeoutSeconds: Int = 10,
    var readTimeoutSeconds: Int = 120,
    var writeTimeoutSeconds: Int = 10,

    var retryMaxAttempts: Int = 3,
    var retryBackoffSeconds: Long = 2,
) {
    val isDummyKey: Boolean
        get() = apiKey.isBlank() || apiKey == "dummy-key"

    val isConfigured: Boolean
        get() = !isDummyKey &&
            voiceId.isNotBlank() &&
            !voiceId.startsWith("\${") &&
            voiceId != "dummy-voice-id"
}
