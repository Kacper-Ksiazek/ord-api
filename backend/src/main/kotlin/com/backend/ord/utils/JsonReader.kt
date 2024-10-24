package com.backend.ord.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

object JsonReader {
    val objectMapper = jacksonObjectMapper()

    inline fun <reified T> readJsonFile(
        pathToJSONFile: String,
        typeReference: TypeReference<T>
    ): T {
        val file = File(pathToJSONFile)
        val fileContent = file.readText()

        return objectMapper.readValue(
            fileContent,
            typeReference
        )
    }
}
