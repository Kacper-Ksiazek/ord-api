package com.ord.core.word.models.word_progress

import com.ord.config.GamesConfig
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Learning progress for an active word")
data class WordProgressDTO(
    @Schema(description = "Accumulated mastery points from games", example = "3")
    val points: Int,

    @Schema(description = "Whether the word reached the completion threshold", example = "false")
    val isCompleted: Boolean,

    @Schema(description = "Timestamp of the current completion state", nullable = true)
    val completedAt: Instant?,

    @Schema(description = "Timestamp of the first time the word was completed", nullable = true)
    val firstCompletedAt: Instant?,
) {
    companion object {
        fun fromEntity(entity: WordProgressEntity): WordProgressDTO {
            val isCompleted = entity.points >= GamesConfig.WordPoints.COMPLETE_WORD_THRESHOLD
            return WordProgressDTO(
                points = entity.points,
                isCompleted = isCompleted,
                completedAt = entity.completedAt,
                firstCompletedAt = entity.firstCompletedAt,
            )
        }
    }
}
