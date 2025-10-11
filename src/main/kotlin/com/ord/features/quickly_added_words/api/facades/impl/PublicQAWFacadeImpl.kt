package com.ord.features.quickly_added_words.api.facades.impl

import com.ord.core.security.UserRepository
import com.ord.features.quickly_added_words.api.facades.PublicQAWFacade
import com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import com.ord.features.quickly_added_words.repositories.QAWRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class PublicQAWFacadeImpl(
    private val qawRepository: QAWRepository,
    private val userRepository: UserRepository,
) : PublicQAWFacade {
    override fun publicBulkCreate(
        body: PublicQAWBulkCreateRequest
    ): Mono<ResponseEntity<List<QuicklyAddedWordEntity>>> {
        return userRepository
            .findByEmail(body.userEmail)
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
            .flatMap { user ->
                val userId = user!!.id!!
                
                val entities = body.words.map { word ->
                    QuicklyAddedWordEntity(
                        word = word,
                        language = body.language,
                        userId = userId
                    )
                }

                qawRepository
                    .saveAll(entities)
                    .collectList()
                    .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
            }
    }
}