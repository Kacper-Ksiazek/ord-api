package com.ord.core.word.models.word_progress

import com.ord.shared.models.IdentifiableUserResource
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("word_progress")
data class WordProgressEntity(
    @Id
    override val id: UUID? = null,

    val wordId: UUID,

    override val userId: UUID,

    var points: Int = 0,
    var completedAt: Instant? = null,
    var firstCompletedAt: Instant? = null,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
) : IdentifiableUserResource
