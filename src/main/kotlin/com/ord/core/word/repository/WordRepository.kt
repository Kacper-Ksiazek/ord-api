package com.ord.core.word.repository

import com.ord.core.word.model.WordEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface WordRepository :
    UserResourceRepository<WordEntity>,
    WordRepositoryCustomMethods