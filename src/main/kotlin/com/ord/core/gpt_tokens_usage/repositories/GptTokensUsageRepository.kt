package com.ord.core.gpt_tokens_usage.repositories

import com.ord.core.gpt_tokens_usage.models.GptTokensUsageEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface GptTokensUsageRepository : UserResourceRepository<GptTokensUsageEntity>
