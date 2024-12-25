package com.backend.ord.domain.mappers.gpt_tokens_usage

import com.backend.ord.domain.dto.gpt_tokens_usage.WordTokensUsageDTO
import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.domain.mappers.bases.MapperBase

interface WordTokensUsageMapper: MapperBase<WordTokensUsage, WordTokensUsageDTO>