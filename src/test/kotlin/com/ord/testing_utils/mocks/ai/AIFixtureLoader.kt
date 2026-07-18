package com.ord.testing_utils.mocks.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.testing_utils.mocks.ai.dto.ArrayStreamFixture
import com.ord.testing_utils.mocks.ai.dto.StringStreamFixture
class AIFixtureLoader(
    private val fixtureRegistry: AIFixtureRegistry,
) {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun <T> loadStructured(operationKey: String, typeReference: TypeReference<T>): T {
        val entry = fixtureRegistry.get(operationKey)
        require(entry.type == AIFixtureType.STRUCTURED) {
            "Fixture $operationKey is not a structured fixture"
        }
        val json = readResource(entry.resourcePath)
        return objectMapper.readValue(json, typeReference)
    }

    fun loadStringStream(operationKey: String): StringStreamFixture {
        val entry = fixtureRegistry.get(operationKey)
        require(entry.type == AIFixtureType.STRING_STREAM) {
            "Fixture $operationKey is not a string stream fixture"
        }
        return objectMapper.readValue(readResource(entry.resourcePath), StringStreamFixture::class.java)
    }

    fun loadArrayStream(operationKey: String): ArrayStreamFixture {
        val entry = fixtureRegistry.get(operationKey)
        require(entry.type == AIFixtureType.ARRAY_STREAM) {
            "Fixture $operationKey is not an array stream fixture"
        }
        return objectMapper.readValue(readResource(entry.resourcePath), ArrayStreamFixture::class.java)
    }

    fun serializeItem(node: JsonNode): String = objectMapper.writeValueAsString(node)

    fun <T> loadStructuredFromJson(json: String, typeReference: TypeReference<T>): T =
        objectMapper.readValue(json, typeReference)

    private fun readResource(path: String): String {
        val stream = javaClass.classLoader.getResourceAsStream(path)
            ?: error("AI fixture resource not found: $path")
        return stream.bufferedReader().use { it.readText() }
    }
}
