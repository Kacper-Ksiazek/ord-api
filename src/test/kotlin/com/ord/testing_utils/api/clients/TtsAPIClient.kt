package com.ord.testing_utils.api.clients

import com.ord.features.tts.api.requests.SpeakRequest
import com.ord.testing_utils.api.APITestClient
import com.ord.testing_utils.dto.MockedAuthenticatedUser
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration

class TtsAPIClient(
    webClient: WebTestClient,
) : APITestClient(webClient) {
    private val baseUrl = "/api/v1/tts"

    fun speak(
        body: SpeakRequest,
        user: MockedAuthenticatedUser? = null,
    ): SpeakStreamResponse {
        val requestSpec = webClient
            .post()
            .uri("$baseUrl/speak")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON, MediaType.parseMediaType("audio/mpeg"))
            .apply {
                if (user != null) {
                    this.cookie(user.authCookie.name, user.authCookie.value)
                }
            }
            .bodyValue(body)

        val result = requestSpec
            .exchange()
            .returnResult(DataBuffer::class.java)

        val status = HttpStatus.valueOf(result.status.value())

        if (status != HttpStatus.OK) {
            return SpeakStreamResponse(
                audioBytes = ByteArray(0),
                status = status,
            )
        }

        val audioBytes = result.responseBody
            .collectList()
            .map { buffers ->
                val outputStream = java.io.ByteArrayOutputStream()
                buffers.forEach { buffer ->
                    val bytes = ByteArray(buffer.readableByteCount())
                    buffer.read(bytes)
                    DataBufferUtils.release(buffer)
                    outputStream.write(bytes)
                }
                outputStream.toByteArray()
            }
            .block(Duration.ofSeconds(30))
            ?: ByteArray(0)

        return SpeakStreamResponse(
            audioBytes = audioBytes,
            status = status,
        )
    }

    data class SpeakStreamResponse(
        val audioBytes: ByteArray,
        val status: HttpStatus,
    )
}
