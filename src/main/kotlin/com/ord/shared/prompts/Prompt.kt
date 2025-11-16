package com.ord.shared.prompts

import java.io.FileNotFoundException

fun List<String>.toParamString(tabulated: Boolean = false): String {
    if (tabulated) {
        return this.joinToString(separator = "\n") { "- $it" }
    }

    return this.joinToString(separator = ", ", prefix = "[", postfix = "]")
}

object PromptCache {
    private val cache: Map<AvailablePrompts, String> = loadAllPrompts()

    private fun loadAllPrompts(): Map<AvailablePrompts, String> {
        return AvailablePrompts.entries.associateWith { promptVariant ->
            val resourcePath = "prompts/${promptVariant.resourcePath}"
            try {
                val resourceStream = PromptCache::class.java.classLoader.getResourceAsStream(resourcePath)
                    ?: throw FileNotFoundException("Resource not found: $resourcePath")

                resourceStream.bufferedReader().use { it.readText() }
            } catch (e: FileNotFoundException) {
                throw Exception("Failed to load prompt file: $resourcePath", e)
            } catch (e: Exception) {
                throw Exception("An error occurred while loading prompt file: $resourcePath", e)
            }
        }
    }

    fun getPromptContent(variant: AvailablePrompts): String {
        return cache[variant] ?: throw IllegalStateException("Prompt not found in cache: $variant")
    }
}

class Prompt(
    variant: AvailablePrompts,
    params: Map<String, String> = mapOf()
) {
    private val finalPromptContent: String
    override fun toString(): String = finalPromptContent

    init {
        try {
            var content: String = PromptCache.getPromptContent(variant)

            params.entries.forEach { (key, value) ->
                val param = "**%%$key%%**"
                content = content.replace(param, value)
            }

            if (content.contains("**%%")) {
                val missingParams = content.split("**%%")
                    .filter { it.contains("%%") }
                    .map { it.substringBefore("%%") }
                    .distinct()

                throw Exception("Missing prompt params: ${missingParams.joinToString(", ")}")
            }

            finalPromptContent = content
        } catch (e: Exception) {
            throw Exception(e.message ?: "An error occurred while processing the prompt", e)
        }
    }
}