package com.ord.features.game.repositories.impl

import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.shared.repositories.GenericUserResourceRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

@Repository
class FinishedGameRepositoryImpl(
    template: R2dbcEntityTemplate
) : GenericUserResourceRepository<FinishedGameEntity>(template) {
    override val entityClass: Class<FinishedGameEntity> = FinishedGameEntity::class.java
}