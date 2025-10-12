package com.ord.core.auth.repositories

import com.ord.core.auth.models.OtpCodeEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface OtpCodeRepository : ReactiveCrudRepository<OtpCodeEntity, UUID> {
    fun findByUserEmail(userEmail: String): Mono<OtpCodeEntity>


    fun deleteByUserEmail(userEmail: String): Mono<Void>
}
