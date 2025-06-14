package com.backend.ord.features.game.services.impl

import com.backend.ord.features.game.repositories.FinishedGameRepository
import com.backend.ord.features.game.services.FinishedGameService
import org.springframework.stereotype.Service

@Service
class FinishedGameServiceImpl(
    override val repository: FinishedGameRepository
) : FinishedGameService