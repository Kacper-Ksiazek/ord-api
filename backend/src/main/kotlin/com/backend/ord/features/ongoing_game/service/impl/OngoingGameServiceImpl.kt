package com.backend.ord.features.ongoing_game.service.impl

import com.backend.ord.features.ongoing_game.repository.OngoingGameRepository
import com.backend.ord.features.ongoing_game.service.OngoingGameService
import org.springframework.stereotype.Service

@Service
class OngoingGameServiceImpl(
    override val repository: OngoingGameRepository
) : OngoingGameService