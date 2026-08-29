package com.ord.core.word.repositories.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word_progress.WordProgressEntity
import com.ord.core.word.repositories.WordProgressRepositoryCustomMethods
import com.ord.core.word.repositories.WordProgressWithWord
import com.ord.shared.domain.dto.CountingSummary
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Repository
class WordProgressRepositoryCustomMethodsImpl(
    template: R2dbcEntityTemplate,
) : WordProgressRepositoryCustomMethods {
    private val databaseClient: DatabaseClient = template.databaseClient

    override fun findAllByOriginsAndUserId(
        origins: Set<String>,
        language: LanguageName,
        userId: UUID,
    ): Flux<WordProgressWithWord> {
        val selectQuery = """
            SELECT wp.id, wp.word_id, wp.user_id, wp.points, wp.completed_at, wp.first_completed_at,
                   wp.created_at, wp.updated_at, w.source_word
            FROM word_progress wp
                INNER JOIN words w ON w.id = wp.word_id
            WHERE w.language = :language
              AND w.status = 'ACTIVE'
              AND w.source_word = ANY(:origins)
              AND wp.user_id = :userId
        """

        return databaseClient.sql(selectQuery)
            .bind("language", language.name)
            .bind("origins", origins.toTypedArray())
            .bind("userId", userId)
            .map { row ->
                WordProgressWithWord(
                    progress = WordProgressEntity(
                        id = row.get("id", UUID::class.java)!!,
                        wordId = row.get("word_id", UUID::class.java)!!,
                        userId = userId,
                        points = row.get("points", Int::class.java)!!,
                        completedAt = row.get("completed_at", Instant::class.java),
                        firstCompletedAt = row.get("first_completed_at", Instant::class.java),
                        createdAt = row.get("created_at", Instant::class.java)!!,
                        updatedAt = row.get("updated_at", Instant::class.java)!!,
                    ),
                    sourceWord = row.get("source_word", String::class.java)!!,
                )
            }
            .all()
    }

    override fun countCompleted(
        language: LanguageName,
        userId: UUID,
    ): Mono<CountingSummary> {
        val query = """
            SELECT * FROM count_words_by_field(
                'first_completed_at',
                cast(:language as text),
                :userId
            )
        """

        return databaseClient.sql(query)
            .bind("language", language.name)
            .bind("userId", userId)
            .fetch()
            .one()
            .map { row ->
                CountingSummary(
                    today = (row["today"] as? Number)?.toInt() ?: 0,
                    week = (row["week"] as? Number)?.toInt() ?: 0,
                    month = (row["month"] as? Number)?.toInt() ?: 0,
                )
            }
    }
}
