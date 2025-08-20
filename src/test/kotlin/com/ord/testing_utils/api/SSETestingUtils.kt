package com.ord.testing_utils.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

class SSETestingUtils(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    fun <TChunk, TFinalContent> postStructuralChunks(
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

    fun postStringChunks(
        request: MockHttpServletRequestBuilder,
        expectedStatus: HttpStatus = HttpStatus.OK,
    ): String {
        val requestBuilder = request
            .characterEncoding("UTF-8")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .contentType(MediaType.APPLICATION_JSON)

        val mvcResult = mockMvc
            .perform(requestBuilder)
            .andExpect(ResultMatcher { result ->
                result.response.status shouldBe expectedStatus.value()
            })
            .andReturn()

        // Abort for testing errors / edge cases
        if (expectedStatus !== HttpStatus.OK) {
            return ""
        }

        val response: MockHttpServletResponse = mvcResult.response
        val rawBody = response.getContentAsString(Charsets.UTF_8)

        // Split SSE events (double newline separated)
        val chunks = rawBody.split("\n\n")
            .filter { it.isNotEmpty() }
            .map { it.refineEventLine(applyTrim = false) }

        return chunks.joinToString("")
    }

    private fun String.refineEventLine(applyTrim: Boolean = true): String {
        val result: String = this.replace("data:", "")
            .apply {
                if (applyTrim) {
                    this.trim()
                }
            }

        return result;
    }
}