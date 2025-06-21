package com.ord.features.game.services.impl

import com.ord.features.game.repositories.FinishedGameRepository
import com.ord.features.game.services.FinishedGameService
import org.springframework.stereotype.Service

@Service
class FinishedGameServiceImpl(
    override val repository: FinishedGameRepository
) : FinishedGameService