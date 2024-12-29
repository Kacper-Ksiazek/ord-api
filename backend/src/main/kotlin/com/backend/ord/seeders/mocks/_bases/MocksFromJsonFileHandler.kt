package com.backend.ord.seeders.mocks._bases

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.interfaces.IdentifiableUserResource
import com.backend.ord.utils.JsonReader
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

private const val ROOT = "./src/main/resources/mocks/"

private fun getAbsolutePath(path: String): String {
    return if (path.startsWith('/')) {
        ROOT + path
    } else {
        "$ROOT/$path"
    }
}

interface MocksFromJsonFileHandler<
        RepositoryTargetType : IdentifiableUserResource, // Eg. Word
        FileContent, // Eg. List<AIGeneratedWordManual>
        JSONDataModelType  // Eg. AIGeneratedWordManual
        > {
    /**
     * The repository that will be used to save the data read from the JSON file
     */
    val repository: JpaRepository<RepositoryTargetType, UUID>

    /**
     * The path to the JSON file that contains the data to be read
     */
    val pathToJSONFile: String

    /**
     * Converts the user to an entity
     */
    fun convertToEntity(jsonData: JSONDataModelType, user: User): RepositoryTargetType

    /**
     * Provides the `TypeReference` for the specific `JSONDataModelType` at runtime.
     */
    fun typeReference(): TypeReference<FileContent>

    /**
     * Reads data from a JSON file and returns a list of JSON data models
     */
    fun readFromJSONFile(): FileContent {
        try {
            return JsonReader.readJsonFile(
                pathToJSONFile = getAbsolutePath(pathToJSONFile),
                typeReference = typeReference()
            )

        } catch (e: java.io.FileNotFoundException) {
            throw Exception("The JSON file was not found: $pathToJSONFile", e)
        } catch (e: Exception) {
            throw Exception("An error occurred while reading the JSON file: $pathToJSONFile", e)
        }
    }

    fun parseFileContent(fileContent: FileContent): List<JSONDataModelType> {
        @Suppress("UNCHECKED_CAST")
        return fileContent as List<JSONDataModelType>;
    }

    /**
     * Seeds the data from the JSON file to the database. Returns the number of records seeded
     */
    fun seedFromJSONFile(user: User): List<RepositoryTargetType> {
        val data = parseFileContent(
            readFromJSONFile()
        ).map {
            this.convertToEntity(
                jsonData = it,
                user = user
            )
        }

        return repository.saveAll(data)
    }
}