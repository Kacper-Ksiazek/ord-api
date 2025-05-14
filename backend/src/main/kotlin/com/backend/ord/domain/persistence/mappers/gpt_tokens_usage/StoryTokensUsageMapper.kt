package com.backend.ord.domain.persistence.mappers.gpt_tokens_usage

import com.backend.ord.domain.persistence.dto.gpt_tokens_usage.StoryTokensUsageDTO
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.StoryTokensUsage
import com.backend.ord.shared.models.MapperBase

interface StoryTokensUsageMapper : MapperBase<StoryTokensUsage, StoryTokensUsageDTO>