package com.backend.ord.repositories

import com.backend.ord.domain.persistence.entities.UserActivityLog
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface UserActivityLogRepository : UserResourceRepository<UserActivityLog> {


}