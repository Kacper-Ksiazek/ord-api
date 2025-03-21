package com.backend.ord.repositories

import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface OngoingGameRepository : UserResourceRepository<OngoingGameRepository>