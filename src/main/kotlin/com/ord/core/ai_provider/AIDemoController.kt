package com.ord.core.ai_provider

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.UUID

@RestController
@RequestMapping("/api/demo")
class AIDemoController(
    private val openAIStreamClientService: OpenAIAPIClientService
) {
    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamResponse(@RequestParam prompt: String): Flux<String> {
        val prompt = "Give me a completely random fun fact. Be creative and make it interesting! "

        val emitter = openAIStreamClientService.openStream(
            prompt = prompt,
            onChunkReceived = { chunk ->
//                println(chunk)
            },
            onComplete = { result ->
                println("Stream completed with result: $result")
            }
        )

        return emitter.asFlux()
    }
}