package com.ord.testing_utils.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

class RESTTestingUtils(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {
    fun <TChunk, TFinalContent> postSSERequest(
        request: MockHttpServletRequestBuilder,
        chunkType: TypeReference<TChunk>,
        finalType: TypeReference<TFinalContent>
    ): Pair<List<TChunk>, TFinalContent> {
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
            .map { it.refineEventLine() }

        val chunks: List<TChunk> = events
            .slice(0 until events.size - 1)
            .map {
                objectMapper.readValue(it, chunkType)
            }

        val finalEvent: TFinalContent = objectMapper.readValue(events.last(), finalType)

        return Pair(chunks, finalEvent)
    }

    private fun String.refineEventLine(): String {
        val result = this.replace("data:", "")
            .trim()

        return result;
    }
}