package com.backend.ord.services.impl

import com.backend.ord.domain.entities.Word
import com.backend.ord.repositories.WordRepository
import com.backend.ord.repositories.bases.UserResourceRepository
import com.backend.ord.services.WordService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class WordServiceImpl(
    override val repository: UserResourceRepository<Word>
) : WordService