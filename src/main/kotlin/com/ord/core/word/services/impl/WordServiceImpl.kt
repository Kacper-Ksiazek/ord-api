package com.ord.core.word.services.impl

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.api.capture.requests.dto.CaptureWordRequest
import com.ord.core.word.api.capture.requests.dto.UpdateCapturedWordRequest
import com.ord.core.word.api.crud.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.crud.requests.enums.WordToggleableProperty
import com.ord.core.word.api.crud.requests.enums.toggleProperty
import com.ord.core.word.api.crud.responses.dto.SingleWordResponse
import com.ord.core.word.models.word.WordDTO
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.WordMapper
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordStatus
import com.ord.core.word.models.word.enums.WordType
import com.ord.core.word.models.word_progress.WordProgressDTO
import com.ord.core.word.repositories.WordRepository
import com.ord.core.word.repositories.WordStatusCounts
import com.ord.core.word.repositories.WordsPaginatedResult
import com.ord.core.word.services.WordProgressService
import com.ord.core.word.services.WordService
import com.ord.exceptions.REST.BadRequestException
import com.ord.exceptions.REST.ConflictException
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.user_activity_log.model.UserActivityLogEntity
import com.ord.features.user_activity_log.model.enums.UserActivityType
import com.ord.features.user_activity_log.service.UserActivityLogService
import com.ord.shared.domain.dto.CountingSummary
import com.ord.shared.domain.enums.SortDirection
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class WordServiceImpl(
    private val wordRepository: WordRepository,
    private val wordMapper: WordMapper,
    private val wordProgressService: WordProgressService,
    private val userActivityLogService: UserActivityLogService,
) : WordService {
    override val repository: WordRepository = wordRepository

    override fun changeBankForSingleWord(wordId: UUID, bankId: UUID?, userId: UUID): Mono<Int> {
        return repository.changeBankForSingleWord(bankId = bankId, wordId = wordId, userId = userId)
            .flatMap { result ->
                if (result == 0) Mono.error(NotFoundException("Word with id $wordId for user with id $userId not found"))
                else Mono.just(result)
            }
    }

    override fun changeBankForMultipleWords(wordIds: List<UUID>, bankId: UUID?, userId: UUID): Mono<Int> {
        return repository.changeBankForMultipleWords(bankId = bankId, wordIds = wordIds, userId = userId)
            .flatMap { result ->
                when {
                    result == 0 -> Mono.error(NotFoundException("No words found for user with id $userId"))
                    else -> Mono.just(result)
                }
            }
    }

    override fun getWordsForPromptGeneration(
        userId: UUID,
        language: LanguageName,
        amountOfLatestWord: Int,
        amountOfProblematicWord: Int,
    ): Mono<Set<String>> {
        val latestWords = repository.findNOfLatestWords(userId, language, amountOfLatestWord).collectList()
        val problematicWords = repository.findNOfMostDifficultWords(userId, language, amountOfProblematicWord).collectList()
        return Mono.zip(latestWords, problematicWords) { latest, problematic -> (latest + problematic).toSet() }
    }

    override fun getWordsForPromptGeneration(
        userId: UUID,
        language: LanguageName,
        banksIds: List<UUID>,
    ): Mono<Set<String>> {
        return repository.findAllWordsFromBanks(userId, language, banksIds).collectList().map { it.toSet() }
    }

    override fun getWordsForGame(
        userId: UUID,
        language: LanguageName,
        completed: Boolean,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
    ): Mono<Set<String>> {
        return repository.getWordsForGame(userId, language, completed, banksIds, bankGroupsIds)
    }

    override fun findManyWords(
        status: WordStatus?,
        completed: Boolean?,
        searchingPhrase: String?,
        bookmarked: Boolean?,
        banksIds: Set<UUID>?,
        bankGroupsIds: Set<UUID>?,
        wordType: WordType?,
        language: LanguageName,
        sortDirection: SortDirection?,
        wordExtraMark: WordExtraMark?,
        sortBy: GetAllWordsSortOptions?,
        userId: UUID,
        page: Int,
        perPage: Int,
    ): Mono<WordsPaginatedResult> {
        return repository.findManyWords(
            userId = userId,
            language = language,
            status = status,
            completed = completed,
            bookmarked = bookmarked,
            wordType = wordType,
            wordExtraMark = wordExtraMark,
            searchingPhrase = searchingPhrase,
            sortDirection = sortDirection ?: SortDirection.DESC,
            sortBy = sortBy ?: GetAllWordsSortOptions.CREATED_AT,
            banksIds = banksIds,
            bankGroupsIds = bankGroupsIds,
            page = page,
            perPage = perPage,
        )
    }

    override fun findOneWord(wordId: UUID, userId: UUID): Mono<SingleWordResponse> {
        return repository.findOneWord(wordId, userId)
    }

    override fun toggleProperty(wordId: UUID, userId: UUID, property: WordToggleableProperty): Mono<WordEntity> {
        return repository.findByIdAndUserId(wordId, userId)
            .switchIfEmpty(Mono.error(NotFoundException("Word with id $wordId not found")))
            .map { it!!.toggleProperty(property) }
            .flatMap { repository.save(it) }
    }

    override fun togglePropertyForManyWords(
        wordIds: Set<UUID>,
        userId: UUID,
        property: WordToggleableProperty,
    ): Flux<WordEntity> {
        return repository.findAllByIdInAndUserId(wordIds, userId)
            .collectList()
            .flatMap { words ->
                if (words.isEmpty()) {
                    Mono.error(NotFoundException("No requested words found for user with id $userId"))
                } else {
                    repository.saveAll(words.map { it.toggleProperty(property) }).collectList()
                }
            }
            .flatMapMany { Flux.fromIterable(it) }
    }

    override fun saveNewActiveWord(word: WordEntity, userId: UUID): Mono<WordDTO> {
        require(word.status == WordStatus.ACTIVE && word.hasActivationFields()) {
            "Active words require type, translation and definition"
        }

        val language = word.language
        return repository.save(word)
            .flatMap { saved ->
                wordProgressService.createInitialProgress(saved.id!!, userId)
                    .map { progress -> wordMapper.toDTO(saved, WordProgressDTO.fromEntity(progress)) }
            }
            .flatMap { dto ->
                countCreated(language, userId).map { countingSummary ->
                    val logs = mutableSetOf<UserActivityLogEntity>()
                    if (countingSummary.today >= 10) {
                        logs.add(
                            UserActivityLogEntity(
                                type = UserActivityType.WORDS_ADDED_IN_ONE_DAY_10,
                                language = language,
                                userId = userId,
                            )
                        )
                    }
                    if (countingSummary.week >= 50) {
                        logs.add(
                            UserActivityLogEntity(
                                type = UserActivityType.WORDS_ADDED_IN_ONE_WEEK_50,
                                language = language,
                                userId = userId,
                            )
                        )
                    }
                    dto to logs
                }
            }
            .flatMap { (dto, logs) ->
                if (logs.isEmpty()) Mono.just(dto)
                else userActivityLogService.logMany(logs).then(Mono.just(dto))
            }
            .onErrorMap(DataIntegrityViolationException::class.java) {
                ConflictException("Word already exists for this user, language, type and source word")
            }
    }

    override fun captureWord(request: CaptureWordRequest, userId: UUID, status: WordStatus): Mono<WordDTO> {
        val entity = WordEntity(
            status = status,
            sourceWord = request.sourceWord,
            language = request.language,
            translation = request.translation,
            definition = request.definition,
            extraMark = request.extraMark,
            type = request.type,
            userId = userId,
        )
        return repository.save(entity)
            .map { wordMapper.toDTO(it) }
            .onErrorMap(DataIntegrityViolationException::class.java) {
                ConflictException("Captured word already exists for this user and language")
            }
    }

    override fun bulkCaptureWords(
        requests: List<CaptureWordRequest>,
        userId: UUID,
        status: WordStatus,
    ): Mono<List<WordDTO>> {
        return Flux.fromIterable(requests)
            .concatMap { captureWord(it, userId, status) }
            .collectList()
    }

    override fun updateCapturedWord(wordId: UUID, userId: UUID, body: UpdateCapturedWordRequest): Mono<WordDTO> {
        return repository.findByIdAndUserId(wordId, userId)
            .switchIfEmpty(Mono.error(NotFoundException("Word with id $wordId not found")))
            .flatMap { entity ->
                val word = entity!!
                if (word.status != WordStatus.CAPTURED) {
                    return@flatMap Mono.error<WordDTO>(BadRequestException("Only captured words can be updated"))
                }
                repository.save(
                    word.copy(
                        sourceWord = body.sourceWord ?: word.sourceWord,
                        translation = body.translation ?: word.translation,
                        definition = body.definition ?: word.definition,
                        extraMark = body.extraMark ?: word.extraMark,
                        type = body.type ?: word.type,
                    ),
                ).map { wordMapper.toDTO(it) }
            }
    }

    override fun bulkUpdateSourceWords(userId: UUID, updates: List<Pair<UUID, String>>): Mono<List<WordDTO>> {
        val updateMap = updates.toMap()
        return repository.findAllByIdInAndUserId(updateMap.keys, userId)
            .collectList()
            .flatMap { entities ->
                if (entities.any { it.status != WordStatus.CAPTURED }) {
                    return@flatMap Mono.error<List<WordEntity>>(
                        BadRequestException("Only captured words can be updated"),
                    )
                }
                val updated = entities.map { entity ->
                    entity.copy(sourceWord = updateMap[entity.id] ?: entity.sourceWord)
                }
                repository.saveAll(updated).collectList()
            }
            .map { entities -> entities.map { wordMapper.toDTO(it) } }
    }

    override fun activateWord(wordId: UUID, userId: UUID): Mono<WordDTO> {
        return repository.findByIdAndUserId(wordId, userId)
            .switchIfEmpty(Mono.error(NotFoundException("Word with id $wordId not found")))
            .flatMap { entity ->
                val word = entity!!
                if (word.status != WordStatus.CAPTURED) {
                    return@flatMap Mono.error<WordDTO>(BadRequestException("Only captured words can be activated"))
                }
                if (!word.hasActivationFields()) {
                    return@flatMap Mono.error<WordDTO>(
                        BadRequestException("Word must have type, translation and definition before activation"),
                    )
                }
                val activated = word.copy(status = WordStatus.ACTIVE)
                repository.save(activated)
                    .flatMap { saved ->
                        wordProgressService.createInitialProgress(saved.id!!, userId)
                            .map { progress -> wordMapper.toDTO(saved, WordProgressDTO.fromEntity(progress)) }
                    }
                    .onErrorMap(DataIntegrityViolationException::class.java) {
                        ConflictException("Active word already exists for this user, language, type and source word")
                    }
            }
    }

    override fun activateManyWords(wordIds: Set<UUID>, userId: UUID): Mono<Unit> {
        if (wordIds.isEmpty()) return Mono.error(BadRequestException("No IDs provided for activation"))
        return Flux.fromIterable(wordIds)
            .concatMap { activateWord(it, userId).then() }
            .then(Mono.just(Unit))
    }

    override fun countByStatus(userId: UUID): Mono<WordStatusCounts> = repository.countByStatus(userId)

    override fun countCreated(language: LanguageName, userId: UUID): Mono<CountingSummary> {
        return repository.countCreated(language, userId)
    }
}
