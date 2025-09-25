package com.ord.core.langugae_proficiency.impl

import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.shared.repositories.GenericUserResourceRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class LanguageProficiencyRepositoryImpl(
    template: R2dbcEntityTemplate,
    private val databaseClient: DatabaseClient
) : GenericUserResourceRepository<LanguageProficiencyEntity>(template) {

    override val entityClass = LanguageProficiencyEntity::class.java
}