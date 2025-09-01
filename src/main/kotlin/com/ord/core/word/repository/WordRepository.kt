package com.ord.core.word.repository

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.model.WordEntity
import com.ord.shared.domain.dto.CountingSummary
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface WordRepository : 
    UserResourceRepository<WordEntity>,
    WordRepositoryCustomMethods {
}