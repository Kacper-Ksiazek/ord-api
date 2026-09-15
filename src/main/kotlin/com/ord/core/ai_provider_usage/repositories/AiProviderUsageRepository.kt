package com.ord.core.ai_provider_usage.repositories

import com.ord.core.ai_provider_usage.models.AiProviderUsageEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface AiProviderUsageRepository : UserResourceRepository<AiProviderUsageEntity>
