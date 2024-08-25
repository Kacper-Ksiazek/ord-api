package com.backend.ord.services.impl.gpt_tokens_usage

import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.repositories.bases.UserResourceRepository
import com.backend.ord.services.gpt_tokens_usage.WordTokensUsageService
import org.springframework.stereotype.Service


@Service
class WodTokensUsageServiceImpl(
    override val repository: UserResourceRepository<WordTokensUsage>
) : WordTokensUsageService