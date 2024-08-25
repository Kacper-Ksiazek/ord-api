package com.backend.ord.services.gpt_tokens_usage

import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.services.bases.UserResourceService
import org.springframework.stereotype.Service

@Service
interface WordTokensUsageService: UserResourceService<WordTokensUsage>
