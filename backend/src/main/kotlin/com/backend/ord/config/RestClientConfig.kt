package com.backend.ord.config

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.core.ai_provider.dto.OpenAIRequest
import com.backend.ord.core.ai_provider.dto.OpenAIResponse
import com.backend.ord.exceptions.OpenAIResponseIsNullException
import com.backend.ord.utils.Console
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

// TODO: Rename to the OpenAIClientConfig or similar
@Configuration
class RestClientConfig(
    private val openAIProperties: OpenAIProperties
) {
    private fun openAITemplate(): RestTemplate {
        val restTemplate = RestTemplate()

        restTemplate.interceptors.add(
            ClientHttpRequestInterceptor { request: HttpRequest,
                                           body: ByteArray,
                                           execution: ClientHttpRequestExecution ->
                request.headers["Authorization"] = openAIProperties.authenticationHeaderValue
                execution.execute(request, body)
            }
        )

        return restTemplate
    }

    fun makeOpenAIPostRequest(request: OpenAIRequest?): OpenAIResponse {
        Console.printPurple("Making OpenAI request...")

        return openAITemplate().postForObject(
            openAIProperties.apiUrl,
            request,
            OpenAIResponse::class.java
        ).let {
            it ?: throw OpenAIResponseIsNullException()
        }
    }
}
