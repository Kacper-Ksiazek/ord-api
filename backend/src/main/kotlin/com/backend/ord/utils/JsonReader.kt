package com.backend.ord.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.io.File

object JsonReader {
    val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    fun <T> readJsonFile(
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
