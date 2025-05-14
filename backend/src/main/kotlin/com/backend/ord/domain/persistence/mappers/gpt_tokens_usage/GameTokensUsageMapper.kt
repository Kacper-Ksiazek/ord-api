package com.backend.ord.domain.persistence.mappers.gpt_tokens_usage

import com.backend.ord.domain.persistence.dto.gpt_tokens_usage.GameTokensUsageDTO
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.shared.models.mappers.MapperBase

interface GameTokensUsageMapper : MapperBase<GameTokensUsage, GameTokensUsageDTO>