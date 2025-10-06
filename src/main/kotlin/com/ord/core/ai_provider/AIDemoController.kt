package com.ord.core.ai_provider

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/demo")
class AIDemoController(
    private val openAIStreamClientService: OpenAIAPIClientService
) {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)

    @GetMapping("/stream-string", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamStringResponse(): Flux<String> {
        val prompt = "Generate 5 random ice breaker questions. Start each with a separate numbered point"

        return openAIStreamClientService.openSimpleStringStream(
            prompt = prompt,
            onComplete = { (payload, emitter) ->
                // TODO: Add gpt tokens usage log here - when the system will be redesigned
                emitter.tryEmitNext(
                    objectMapper.writeValueAsString(
                        payload
                    )
                )
            }
        )
    }

    @GetMapping("/stream-array", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamArrayResponse(): Flux<String> {
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

        return openAIStreamClientService.openStructuredArrayStream<DemoArray>(
            prompt = prompt,
            streamedItemType = object : TypeReference<DemoArray>() {},
            onComplete = { (payload, emitter) ->
                // TODO: Add gpt tokens usage log here - when the system will be redesigned
                emitter.tryEmitNext(
                    objectMapper.writeValueAsString(
                        payload
                    )
                )
            }
        )
    }
}