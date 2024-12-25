package com.backend.ord.domain.mappers.gpt_tokens_usage

import com.backend.ord.domain.dto.gpt_tokens_usage.GameTokensUsageDTO
import com.backend.ord.domain.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.domain.mappers.bases.MapperBase

interface GameTokensUsageMapper : MapperBase<GameTokensUsage, GameTokensUsageDTO>