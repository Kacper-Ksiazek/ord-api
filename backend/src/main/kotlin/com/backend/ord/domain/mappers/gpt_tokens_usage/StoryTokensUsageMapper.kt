package com.backend.ord.domain.mappers.gpt_tokens_usage

import com.backend.ord.domain.dto.gpt_tokens_usage.StoryTokensUsageDTO
import com.backend.ord.domain.entities.gpt_tokens_usage.StoryTokensUsage
import com.backend.ord.domain.mappers.bases.MapperBase

interface StoryTokensUsageMapper : MapperBase<StoryTokensUsage, StoryTokensUsageDTO>