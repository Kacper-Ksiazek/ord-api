package com.ord.features.game.repositories.impl

import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.shared.repositories.GenericUserResourceRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

@Repository
class OngoingGameRepositoryImpl(
    template: R2dbcEntityTemplate
) : GenericUserResourceRepository<OngoingGameEntity>(template) {
    override val entityClass: Class<OngoingGameEntity> = OngoingGameEntity::class.java
}