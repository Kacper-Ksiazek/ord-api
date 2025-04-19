package com.backend.ord.seeders.mocks.bases

import com.backend.ord.utils.JsonReader
import com.fasterxml.jackson.core.type.TypeReference

enum class RootDir(val path: String) {
    MAIN_APP(path = "./src/main/resources/"),
    TEST_RESOURCES(path = "./src/test/resources/");
}

interface ResourceJSONFileReader<
        FileContent, // Eg. List<AIGeneratedWordManual>
        JSONDataModelType  // Eg. AIGeneratedWordManual
        > {
    val root: RootDir
        get() = RootDir.MAIN_APP

    val pathToJSONFile: String

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
            root.path + path
        } else {
            "${root.path}/$path"
        }
    }
}