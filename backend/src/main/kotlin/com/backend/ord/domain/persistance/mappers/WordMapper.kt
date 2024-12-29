package com.backend.ord.domain.persistance.mappers

import com.backend.ord.domain.persistance.dto.WordDTO
import com.backend.ord.domain.persistance.entities.Word
import com.backend.ord.domain.persistance.mappers.bases.MapperBase

interface WordMapper: MapperBase<Word, WordDTO>