package com.backend.ord.seeders.mocks.bases

import com.backend.ord.utils.JsonReader
import com.fasterxml.jackson.core.type.TypeReference

private const val ROOT = "./src/main/resources/mocks/"

interface ResourceJSONFileReader<
        FileContent, // Eg. List<AIGeneratedWordManual>
        JSONDataModelType  // Eg. AIGeneratedWordManual
        > {
    /**
     * The path to the JSON file that contains the data to be read
     */
    val pathToJSONFile: String

    /**
     * Provides the `TypeReference` for the specific `JSONDataModelType` at runtime.
     */
    fun typeReference(): TypeReference<FileContent>

    /**
     * Reads data from a JSON file and returns a list of JSON data models
     */
    fun readFromJSONFile(): List<JSONDataModelType> {
        @Suppress("UNCHECKED_CAST")
        return JsonReader.readJsonFile(
            pathToJSONFile = getAbsolutePath(pathToJSONFile),
            typeReference = typeReference()
        ) as List<JSONDataModelType>
    }

    private fun getAbsolutePath(path: String): String {
        return if (path.startsWith('/')) {
            ROOT + path
        } else {
            "$ROOT/$path"
        }
    }
}