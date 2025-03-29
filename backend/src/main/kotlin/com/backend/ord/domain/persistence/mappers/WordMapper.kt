package com.backend.ord.domain.persistence.mappers

import com.backend.ord.domain.persistence.dto.WordDTO
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.domain.persistence.mappers.bases.MapperBase

interface WordMapper : MapperBase<Word, WordDTO>