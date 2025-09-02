package com.ord.core.word.repository

import com.ord.core.word.model.WordEntity
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Repository

@Repository
interface WordRepository : 
    UserResourceRepository<WordEntity>,
    WordRepositoryCustomMethods {
}