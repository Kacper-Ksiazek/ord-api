package com.backend.ord.domain.persistence.dto.game

import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.enums.persistence.game.GameStatus
import com.backend.ord.enums.persistence.game.GameType
import com.backend.ord.enums.persistence.language.LanguageName
import java.time.Instant
import java.util.*

abstract class GameDTOBase<Instructions>(
    open val id: UUID = UUID.randomUUID(),

    open var finalScore: Int = 0,
    open var duration: String = "00:00:00",

    open var instruction: Instructions,


    open var type: GameType,
    open var grade: GameGrade = GameGrade.NA,
    open var language: LanguageName,
    open var difficulty: GameDifficulty,
    open var status: GameStatus = GameStatus.IN_PROGRESS,

    open val user: UserDTO,

    open val createdAt: Instant = Instant.now(),
    open var updatedAt: Instant = Instant.now()
)


