package com.ord.shared.api

import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

object MonoUtils {
    fun <T> fromBlocking(blockingSupplier: () -> ResponseEntity<T>): Mono<ResponseEntity<T>> {
        return Mono.fromCallable(blockingSupplier)
            .subscribeOn(Schedulers.boundedElastic())
    }
}