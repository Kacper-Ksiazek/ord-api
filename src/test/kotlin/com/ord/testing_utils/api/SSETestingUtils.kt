package com.ord.testing_utils.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

class SSETestingUtils(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {
    fun <TChunk, TFinalContent> postSSERequestWithStructuralChunks(
        request: MockHttpServletRequestBuilder,
        chunkType: TypeReference<TChunk>,
        finalType: TypeReference<TFinalContent>,
        expectedStatus: HttpStatus = HttpStatus.OK,
    ): Pair<List<TChunk>, TFinalContent>? {
        val mvcResult = mockMvc
            .perform(request)
            .andExpect(ResultMatcher { result ->
                result.response.status shouldBe expectedStatus.value()
            })
            .andReturn()

        // Abort for testing errors / edge cases
        if (expectedStatus !== HttpStatus.OK) {
            return null
        }

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