package com.backend.ord.domain.persistance.mappers.gpt_tokens_usage

import com.backend.ord.domain.persistance.dto.gpt_tokens_usage.WordTokensUsageDTO
import com.backend.ord.domain.persistance.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.domain.persistance.mappers.bases.MapperBase

interface WordTokensUsageMapper: MapperBase<WordTokensUsage, WordTokensUsageDTO>