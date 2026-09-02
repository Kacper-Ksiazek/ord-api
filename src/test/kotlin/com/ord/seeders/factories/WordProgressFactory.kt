package com.ord.seeders.factories

import com.ord.core.word.models.word_progress.WordProgressEntity
import com.ord.seeders.factories.bases.FactoryBase
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class WordProgressFactory : FactoryBase() {
    fun mockEntity(
        wordId: UUID,
        userId: UUID,
        points: Int = 0,
        completedAt: Instant? = null,
        firstCompletedAt: Instant? = null,
    ): WordProgressEntity {
        return WordProgressEntity(
            wordId = wordId,
            userId = userId,
            points = points,
            completedAt = completedAt,
            firstCompletedAt = firstCompletedAt,
        )
    }
}
