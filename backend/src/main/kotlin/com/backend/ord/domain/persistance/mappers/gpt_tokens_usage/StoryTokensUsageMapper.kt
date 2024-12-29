package com.backend.ord.domain.persistance.mappers.gpt_tokens_usage

import com.backend.ord.domain.persistance.dto.gpt_tokens_usage.StoryTokensUsageDTO
import com.backend.ord.domain.persistance.entities.gpt_tokens_usage.StoryTokensUsage
import com.backend.ord.domain.persistance.mappers.bases.MapperBase

interface StoryTokensUsageMapper : MapperBase<StoryTokensUsage, StoryTokensUsageDTO>