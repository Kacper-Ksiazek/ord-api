package com.backend.ord.services.impl

import com.backend.ord.repositories.OngoingGameRepository
import com.backend.ord.services.OngoingGameService
import org.springframework.stereotype.Service

@Service
class OngoingGameServiceImpl(
    override val repository: OngoingGameRepository
) : OngoingGameService
