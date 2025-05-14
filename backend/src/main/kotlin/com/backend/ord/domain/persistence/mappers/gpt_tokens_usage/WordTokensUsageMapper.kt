package com.backend.ord.domain.persistence.mappers.gpt_tokens_usage

import com.backend.ord.domain.persistence.dto.gpt_tokens_usage.WordTokensUsageDTO
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.shared.models.mappers.MapperBase

interface WordTokensUsageMapper : MapperBase<WordTokensUsage, WordTokensUsageDTO>