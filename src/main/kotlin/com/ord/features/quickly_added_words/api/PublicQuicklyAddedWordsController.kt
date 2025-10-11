package com.ord.features.quickly_added_words.api

import com.ord.features.quickly_added_words.api.facades.PublicQAWFacade
import com.ord.features.quickly_added_words.api.requests.PublicQAWBulkCreateRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/public/quickly-added-words")
class PublicQuicklyAddedWordsController(
    private val publicQAWFacade: PublicQAWFacade,
) {
    @PostMapping("/bulk-create")
    fun publicBulkCreate(
        @Valid @RequestBody body: PublicQAWBulkCreateRequest
    ): Mono<ResponseEntity<List<QuicklyAddedWordEntity>>> = publicQAWFacade.publicBulkCreate(body)
}