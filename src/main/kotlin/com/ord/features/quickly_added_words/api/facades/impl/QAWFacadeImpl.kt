package com.ord.features.quickly_added_words.api.facades.impl

import com.ord.features.quickly_added_words.api.facades.QAWFacade
import com.ord.features.quickly_added_words.api.requests.ApproveManyQAWRequest
import com.ord.features.quickly_added_words.api.requests.CreateQAWRequest
import com.ord.features.quickly_added_words.api.requests.UpdateQAWRequest
import com.ord.features.quickly_added_words.model.QuicklyAddedWordDTO
import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import com.ord.features.quickly_added_words.model.QuicklyAddedWordMapper
import com.ord.features.quickly_added_words.repositories.QAWRepository
import com.ord.shared.api.dto.responses.PaginatedDataResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class QAWFacadeImpl(
    private val qawRepository: QAWRepository,
    private val qawMapper: QuicklyAddedWordMapper,
) : QAWFacade {
    /*
     * CREATE
     */

    override fun createOne(
        userId: UUID,
        body: CreateQAWRequest,
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>> {
        val entity = QuicklyAddedWordEntity(
            word = body.word,
            language = body.language,
            translation = body.translation,
            definition = body.definition,
            extraMark = body.extraMark,
            type = body.type,
            isApproved = true,
            userId = userId,
        )

        return qawRepository
            .save(entity)
            .map { qawMapper.toDTO(it) }
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }


    override fun bulkCreate(
        userId: UUID,
        body: List<CreateQAWRequest>,
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>> {
        val entities = body.map { request ->
            QuicklyAddedWordEntity(
                word = request.word,
                language = request.language,
                translation = request.translation,
                definition = request.definition,
                extraMark = request.extraMark,
                type = request.type,
                isApproved = true,
                userId = userId,
            )
        }

        return qawRepository
            .saveAll(entities)
            .map { qawMapper.toDTO(it) }
            .collectList()
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }


    /*
     * READ
     */

    override fun getManyQAWs(
        userId: UUID,
        page: Int?,
        perPage: Int?
    ): Mono<ResponseEntity<PaginatedDataResponse<QuicklyAddedWordDTO>>> {
        return qawRepository
            .findManyQAWs(
                userId = userId,
                page = page,
                perPage = perPage
            )
            .map { paginatedResponse ->
                PaginatedDataResponse(
                    pagination = paginatedResponse.pagination,
                    data = paginatedResponse.data.map { qawMapper.toDTO(it) }
                )
            }
            .map { ResponseEntity.ok(it) }
    }


    /*
     * UPDATE
     */

    override fun updateOne(
        userId: UUID,
        qawId: UUID,
        body: UpdateQAWRequest
    ): Mono<ResponseEntity<QuicklyAddedWordDTO>> {
        return qawRepository
            .findByIdAndUserId(qawId, userId)
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Quickly added word not found")))
            .flatMap { entity ->
                val updatedEntity = entity!!.copy(
                    word = body.updatedWord ?: entity.word,
                    translation = body.translation ?: entity.translation,
                    definition = body.definition ?: entity.definition,
                    extraMark = body.extraMark ?: entity.extraMark,
                    type = body.type ?: entity.type
                )
                qawRepository.save(updatedEntity)
            }
            .map { qawMapper.toDTO(it) }
            .map { ResponseEntity.ok(it) }
    }


    override fun bulkUpdate(
        userId: UUID,
        body: List<Pair<UUID, String>>
    ): Mono<ResponseEntity<List<QuicklyAddedWordDTO>>> {
        val idsToUpdate = body.map { it.first }.toSet()
        val updateMap = body.toMap()

        return qawRepository
            .findAllByIdInAndUserId(idsToUpdate, userId)
            .map { entity ->
                val newWord = updateMap[entity.id]
                if (newWord != null) {
                    entity.copy(word = newWord)
                } else {
                    entity
                }
            }
            .collectList()
            .flatMap { updatedEntities ->
                qawRepository.saveAll(updatedEntities).collectList()
            }
            .map { savedEntities ->
                savedEntities.map { qawMapper.toDTO(it) }
            }
            .map { ResponseEntity.ok(it) }
    }


    override fun approveMany(
        userId: UUID,
        body: ApproveManyQAWRequest
    ): Mono<ResponseEntity<Unit>> {
        return qawRepository
            .approveManyByIdsAndUserId(body.ids.toSet(), userId)
            .map { ResponseEntity.ok().build<Unit>() }
    }

    /*
     * DELETE
     */

    override fun deleteOne(
        userId: UUID,
        qawId: UUID
    ): Mono<ResponseEntity<Unit>> {
        return qawRepository
            .findByIdAndUserId(qawId, userId)
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Quickly added word not found")))
            .flatMap { qawRepository.deleteByIdAndUserId(qawId, userId) }
            .then(Mono.fromCallable { ResponseEntity.ok().build<Unit>() })
    }


    override fun bulkDelete(
        userId: UUID,
        body: List<UUID>
    ): Mono<ResponseEntity<Unit>> {
        val idsToDelete = body.toSet()

        return qawRepository
            .findAllByIdInAndUserId(idsToDelete, userId)
            .map { it.id!! }
            .collectList()
            .flatMap { foundIds ->
                Flux.fromIterable(foundIds)
                    .flatMap { id -> qawRepository.deleteByIdAndUserId(id, userId) }
                    .then(Mono.fromCallable { ResponseEntity.ok().build<Unit>() })
            }
    }
}
