package com.ord.shared.utils

import tools.jackson.core.type.TypeReference
import java.io.File
import java.io.FileNotFoundException

object JsonReader {
    val objectMapper = OrdJsonMapper.instance

    fun <T> readJsonFile(
        pathToJSONFile: String,
        typeReference: TypeReference<T>
    ): T {
        try {
            val file = File(pathToJSONFile)
            val fileContent = file.readText()

            return objectMapper.readValue(
                fileContent,
                typeReference
            )
        } catch (e: FileNotFoundException) {
            throw Exception("The JSON file was not found: $pathToJSONFile", e)
        } catch (e: Exception) {
            throw Exception("An error occurred while reading the JSON file: $pathToJSONFile", e)
        }
    }
}
