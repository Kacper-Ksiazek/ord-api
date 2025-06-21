package com.ord.utils.resource_readers

import com.ord.core.user.model.UserEntity
import com.ord.core.word.model.WordEntity
import com.ord.core.word.repository.WordRepository
import com.ord.shared.utils.JsonReader
import com.ord.testing_utils.dto.resources.db_rows.WordDBExportedRow
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
    user: UserEntity,
    wordsRepository: WordRepository? = null,
    /** If null, all words will be loaded */
    numberOfWordsToLoad: Int? = null
): List<WordEntity> {
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
