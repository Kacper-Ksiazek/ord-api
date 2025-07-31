package com.ord.shared.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.io.FileNotFoundException

object JsonReader {
    val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

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
