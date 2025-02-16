package com.backend.ord.domain.persistence.dto.game

import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.persistence.embedded.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import java.time.Instant
import java.util.*

data class CrosswordGameDTO(
    override val id: UUID = UUID.randomUUID(),
    override var finalScore: Int = 0,
    override var duration: String = "00:00:00",

    override var instruction: CrosswordInstruction,
    override var properAnswers: CrosswordProperAnswers,

    override var type: GameType,
    override var grade: GameGrade,
    override var language: LanguageName,
    override var difficulty: GameDifficulty,
    override var status: GameStatus = GameStatus.IN_PROGRESS,

    override val user: UserDTO,

    override val createdAt: Instant = Instant.now(),
    override var updatedAt: Instant = Instant.now()
) : GameDTOBase<CrosswordInstruction, CrosswordProperAnswers>(
    id = id,
    finalScore = finalScore,
    difficulty = difficulty,
    instruction = instruction,
    properAnswers = properAnswers,
    language = language,
    duration = duration,
    grade = grade,
    type = type,
    status = status,
    user = user,
    createdAt = createdAt,
    updatedAt = updatedAt
)
