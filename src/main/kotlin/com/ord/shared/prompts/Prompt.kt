package com.ord.shared.prompts

import java.io.File
import java.io.FileNotFoundException

fun List<String>.toParamString(tabulated: Boolean = false): String {
    if (tabulated) {
        return this.joinToString(separator = "\n") { "- $it" }
    }

    return this.joinToString(separator = ", ", prefix = "[", postfix = "]")
}

class Prompt(
    variant: AvailablePrompts,
    params: Map<String, String> = mapOf()
) {
    private val promptFilePath = "./src/main/resources/prompts/${variant.resourcePath}";

    private val finalPromptContent: String;
    override fun toString(): String = finalPromptContent

    init {
        try {
            val file = File(promptFilePath)
            var content: String = file.readText()

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
        } catch (e: FileNotFoundException) {
            throw Exception("The prompt file was not found: $promptFilePath", e)
        } catch (e: Exception) {
            throw Exception(e.message ?: "An error occurred while reading the prompt file: $promptFilePath", e)
        }
    }
}