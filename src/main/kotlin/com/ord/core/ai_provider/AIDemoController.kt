package com.ord.core.ai_provider

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/demo")
class AIDemoController(
    private val openAIStreamClientService: OpenAIAPIClientService
) {
    @GetMapping("/stream-string", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamStringResponse(): Flux<String> {
        val prompt = "Generate 5 random ice breaker questions. Start each with a separate numbered point"

        val emitter = openAIStreamClientService.openSimpleStringStream(
            prompt = prompt,
            onChunkReceived = { chunk ->
//                println(chunk)
            },
            onComplete = { payload ->
                // TODO: Add gpt tokens usage log here - when the system will be redesigned
                println("Stream completed with result: ${payload.finalContent}")
            }
        )

        return emitter.asFlux()
    }

    @GetMapping("/stream-array", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamArrayResponse(): Flux<DemoArray> {
        val prompt =
            """
                Generate 5 random ice breaker questions. 
                Each question should be in JSON matching the following TS structure
                
                ```TS
                {
                    "question": "Question text",
                    "answer": "Answer text"
                }
                ```
                
                I want each fragment to be able standalone stringified JSON value able to be put into objectMapper.readValue from jackson.
                
                Between each question and at the dead end, use "[[BREAK]]" block to separate them. It will be used to split the questions into separate items during streaming.
                
                Sample of response:
                
               {"question":"What's your favorite weekend activity?","answer":"I enjoy hiking and spending time in nature."}
               [[BREAK]]
               {"question":"If you could instantly master any skill, what would it be?","answer":"Playing the piano."}
               [[BREAK]]
            """

        val emitter = openAIStreamClientService.openStructuredArrayStream<DemoArray>(
            prompt = prompt,
            streamedItemTypeReference = object : TypeReference<DemoArray>() {},
            onItemReceived = { item ->
                println("✅")
                println(item)
            },
            onComplete = { payload ->
                // TODO: Add gpt tokens usage log here - when the system will be redesigned
                println("Stream completed with result: ${payload.finalContent}")
            }
        )

        return emitter.asFlux()
    }
}