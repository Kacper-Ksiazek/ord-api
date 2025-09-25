package com.ord.features.user_activity_log.repository.impl

import com.ord.features.user_activity_log.model.UserActivityLogEntity
import com.ord.shared.repositories.GenericUserResourceRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

@Repository
class UserActivityLogRepositoryImpl(
    template: R2dbcEntityTemplate,
) : GenericUserResourceRepository<UserActivityLogEntity>(template) {
    override val entityClass: Class<UserActivityLogEntity> = UserActivityLogEntity::class.java
}