package com.backend.ord.config

import com.backend.ord.api.requests.openai.OpenAIRequest
import com.backend.ord.api.responses.openai.OpenAIResponse
import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.exceptions.OpenAIResponseIsNullException
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

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
        return openAITemplate().postForObject(
            openAIProperties.apiUrl,
            request,
            OpenAIResponse::class.java
        ).let {
            it ?: throw OpenAIResponseIsNullException()
        }
    }
}
