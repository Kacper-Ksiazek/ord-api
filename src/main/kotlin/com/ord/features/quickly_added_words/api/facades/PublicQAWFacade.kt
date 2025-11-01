package com.ord.features.quickly_added_words.api.facades

import com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface PublicQAWFacade {
    fun publicBulkCreate(
        body: PublicQAWBulkCreateRequest
    ): Mono<ResponseEntity<Void>>
}