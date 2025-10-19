package com.ord.core.word.repositories

import com.ord.core.word.models.word.WordEntity
import com.ord.shared.repositories.UserResourceRepository

interface WordRepository :
    UserResourceRepository<WordEntity>,
    WordRepositoryCustomMethods