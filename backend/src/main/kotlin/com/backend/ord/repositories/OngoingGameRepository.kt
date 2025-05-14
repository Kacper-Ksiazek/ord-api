package com.backend.ord.repositories

import com.backend.ord.domain.persistence.entities.OngoingGame
import com.backend.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface OngoingGameRepository : UserResourceRepository<OngoingGame>