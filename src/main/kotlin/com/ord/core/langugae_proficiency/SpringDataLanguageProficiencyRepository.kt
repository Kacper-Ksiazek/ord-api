package com.ord.core.langugae_proficiency

import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface SpringDataLanguageProficiencyRepository : ReactiveCrudRepository<LanguageProficiencyEntity, UUID>