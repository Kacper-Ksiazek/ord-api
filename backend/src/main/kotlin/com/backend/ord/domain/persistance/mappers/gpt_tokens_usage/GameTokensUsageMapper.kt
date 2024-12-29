package com.backend.ord.domain.persistance.mappers.gpt_tokens_usage

import com.backend.ord.domain.persistance.dto.gpt_tokens_usage.GameTokensUsageDTO
import com.backend.ord.domain.persistance.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.domain.persistance.mappers.bases.MapperBase

interface GameTokensUsageMapper : MapperBase<GameTokensUsage, GameTokensUsageDTO>