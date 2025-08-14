package com.ord.testing_utils.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import java.util.function.Consumer

class RESTTestingUtils(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {
    fun <T> postSSERequest(
        request: MockHttpServletRequestBuilder,
//        finalType: TypeReference<T>,
        perChunk: Consumer<String> = Consumer { }
        // TODO: Add proper return type here
    ) {
        val mvcResult = mockMvc
            .perform(request)
            .andExpect(ResultMatcher { result ->
                if (result.response.status != 200) {
                    throw AssertionError("Expected HTTP 200 but got ${result.response.status}")
                }
            })
            .andReturn()

        val response: MockHttpServletResponse = mvcResult.response
        val rawBody = response.contentAsString

        // Split SSE events (double newline separated)
        val events = rawBody.split("\n\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val combinedResult = StringBuilder()

        for (event in events) {
            val dataLine = event.lines()
                .firstOrNull { it.startsWith("data:") }
                ?.removePrefix("data:")
                ?.trim()

            if (!dataLine.isNullOrEmpty()) {
                perChunk.accept(dataLine)
                combinedResult.append(dataLine)
            }
        }

        println(combinedResult.toString())


        // Parse final combined JSON into the desired type
//        return objectMapper.readValue(combinedResult.toString(), finalType)
    }
}