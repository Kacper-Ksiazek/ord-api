package com.backend.ord.seeders.mocks.words.bases

import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.interfaces.IdentifiableUserResource
import com.backend.ord.repositories.bases.UserResourceRepository
import com.backend.ord.utils.JsonReader
import com.fasterxml.jackson.core.type.TypeReference

interface MocksFromJsonFileHandler<
        RepositoryTargetType : IdentifiableUserResource, // Eg. Word
        JSONDataModelType  // Eg. AIGeneratedWordManual
        > {
    /**
     * The repository that will be used to save the data read from the JSON file
     */
    val repository: UserResourceRepository<RepositoryTargetType>

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
    fun typeReference(): TypeReference<List<JSONDataModelType>>

    /**
     * Reads data from a JSON file and returns a list of JSON data models
     */
    fun readFromJSONFile(): List<JSONDataModelType> {
        try {
            return JsonReader.readJsonFile(
                pathToJSONFile = pathToJSONFile,
                typeReference = typeReference()
            )

        } catch (e: java.io.FileNotFoundException) {
            throw Exception("The JSON file was not found: $pathToJSONFile", e)
        } catch (e: Exception) {
            throw Exception("An error occurred while reading the JSON file: $pathToJSONFile", e)
        }
    }

    /**
     * Seeds the data from the JSON file to the database. Returns the number of records seeded
     */
    fun seedFromJSONFile(user: User): Int {
        val data = readFromJSONFile().map {
            this.convertToEntity(
                jsonData = it,
                user = user
            )
        }

        repository.saveAll(data)

        return data.size
    }
}