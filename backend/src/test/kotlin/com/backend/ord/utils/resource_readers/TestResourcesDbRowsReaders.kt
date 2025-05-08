package com.backend.ord.utils.resource_readers

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.repositories.WordRepository
import com.backend.ord.testing_utils.dto.resources.db_rows.WordDBExportedRow
import com.backend.ord.utils.JsonReader
import com.fasterxml.jackson.core.type.TypeReference

private const val ROOT = "./src/test/resources/db_rows"

private fun getAbsolutePath(path: String): String {
    return if (path.startsWith('/')) {
        ROOT + path
    } else {
        "$ROOT/$path"
    }
}

/**
 * Load words from a JSON file located in the test resources directory.
 * The file contains 12 rows of words.
 */
fun loadWordsFromResourceFile(
    user: User,
    wordsRepository: WordRepository? = null,
    /** If null, all words will be loaded */
    numberOfWordsToLoad: Int? = null
): List<Word> {
    val path = getAbsolutePath("/words_24_rows.json")
    val typeReference = object : TypeReference<List<WordDBExportedRow>>() {}

    val result = JsonReader.readJsonFile(
        pathToJSONFile = path,
        typeReference = typeReference
    ).map {
        it.convertIntoWordEntity(user)
    }.let {
        if (numberOfWordsToLoad != null) {
            it.take(numberOfWordsToLoad)
        } else {
            it
        }
    }

    wordsRepository?.saveAll(result)

    return result
}
