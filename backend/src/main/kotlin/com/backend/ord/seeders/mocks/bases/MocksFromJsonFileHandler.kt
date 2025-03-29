package com.backend.ord.seeders.mocks.bases

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.interfaces.IdentifiableUserResource
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MocksFromJsonFileHandler<
        RepositoryTargetType : IdentifiableUserResource, // Eg. Word
        FileContent, // Eg. List<AIGeneratedWordManual>
        JSONDataModelType  // Eg. AIGeneratedWordManual
        > : ResourceJSONFileReader<FileContent, JSONDataModelType> {
    /**
     * The repository that will be used to save the data read from the JSON file
     */
    val repository: JpaRepository<RepositoryTargetType, UUID>

    /**
     * Converts the user to an entity
     */
    fun convertToEntity(jsonData: JSONDataModelType, user: User): RepositoryTargetType

    /**
     * Seeds the data from the JSON file to the database. Returns the number of records seeded
     */
    fun seedFromJSONFile(user: User): List<RepositoryTargetType> {
        val data = readFromJSONFile().map {
            this.convertToEntity(
                jsonData = it,
                user = user
            )
        }

        return repository.saveAll(data)
    }
}