package com.ord.utils.resource_readers

import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.repositories.WordProgressRepository
import com.ord.core.word.repositories.WordRepository
import com.ord.seeders.factories.WordProgressFactory
import com.ord.shared.utils.JsonReader
import com.ord.testing_utils.dto.resources.db_rows.WordDBExportedRow
import tools.jackson.core.type.TypeReference
import java.util.UUID

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
    userId: UUID,
    wordsRepository: WordRepository,
    wordProgressRepository: WordProgressRepository,
    wordProgressFactory: WordProgressFactory,
    /** If null, all words will be loaded */
    numberOfWordsToLoad: Int? = null,
): List<WordEntity> {
    val path = getAbsolutePath("/words_24_rows.json")
    val typeReference = object : TypeReference<List<WordDBExportedRow>>() {}

    val rows = JsonReader.readJsonFile(
        pathToJSONFile = path,
        typeReference = typeReference,
    ).let {
        if (numberOfWordsToLoad != null) {
            it.take(numberOfWordsToLoad)
        } else {
            it
        }
    }

    val result = rows.map { it.convertIntoWordEntity(userId) }
    val saved = wordsRepository.saveAll(result).collectList().block()!!

    val progressEntities = saved
        .zip(rows)
        .map { (word, row) ->
            wordProgressFactory.mockEntity(
                wordId = word.id!!,
                userId = userId,
                points = row.progressPoints,
            )
        }

    if (progressEntities.isNotEmpty()) {
        wordProgressRepository.saveAll(progressEntities).collectList().block()
    }

    return saved
}
