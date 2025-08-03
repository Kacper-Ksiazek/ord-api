package com.ord.core.ai_provider

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@RestController
@RequestMapping("/api/demo")
class AIDemoController(
    private val openAIStreamClientService: OpenAIAPIClientService
) {
    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamResponse(@RequestParam prompt: String): Flux<String> {
        val emitter = Sinks.many().unicast().onBackpressureBuffer<String>()

        val prompt = "Tell me a joke about polish people"

        openAIStreamClientService.openStream(
            prompt = prompt,
            onChunkReceived = { chunk ->
                println(chunk)
                // TODO: Next steps - create an enum of possible chunk response types and handle each separately
                // TODO 2: Learn what is `"response.content_part.done"` and `"response.output_item.added"`
                // TODO 3: Create a generic handler for all types of chunks

                emitter.tryEmitNext(chunk)
            },
            onComplete = {
                emitter.tryEmitComplete()
            },
            onError = { error ->
                emitter.tryEmitError(error)
            }
        )

        return emitter.asFlux()
    }
}