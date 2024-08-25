package com.backend.ord.repositories.gpt_tokens_usage

import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface WordTokensUsageRepository : UserResourceRepository<WordTokensUsage>