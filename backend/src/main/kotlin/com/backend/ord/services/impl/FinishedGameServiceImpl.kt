package com.backend.ord.services.impl

import com.backend.ord.repositories.FinishedGameRepository
import com.backend.ord.services.FinishedGameService
import org.springframework.stereotype.Service

@Service
class FinishedGameServiceImpl(
    override val repository: FinishedGameRepository
) : FinishedGameService