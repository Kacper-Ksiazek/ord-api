package com.backend.ord.controllers

import com.backend.ord.api.requests.word.CreateWordRequest
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.mappers.BankMapper
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.WordMapper
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.services.BankService
import com.backend.ord.services.WordService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/words")
class WordController(
    private val jwtService: JwtService,
    private val bankService: BankService,
    private val wordMapper: WordMapper,
    private val userMapper: UserMapper,
    private val wordService: WordService,
    private val bankMapper: BankMapper
) {
    @GetMapping("/")
    fun getAllWords(
        request: HttpServletRequest
    ): ResponseEntity<List<WordDTO>> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val words: List<WordDTO> = wordMapper.toDTOList(
            wordService.findAll(userId = user.id)
        )

        return ResponseEntity.ok(words)
    }

    @GetMapping("/{id}")
    fun getWordById(
        @PathVariable id: String
    ) {
        // Get a word by its ID
    }

    @PostMapping("/")
    fun createWord(
        request: HttpServletRequest,
        @Valid @RequestBody body: CreateWordRequest
    ): ResponseEntity<WordDTO> {
        val user: User = jwtService.getAuthenticatedUser(request)!!

        if (body.bankToCreate != null && body.bankId != null) {
            throw BadRequestException("You cannot create a new bank and use an existing bank at the same time")
        }

        val bank = try {
            bankService.findByIdOrCreate(
                bankId = body.bankId,
                bankToCreate = body.bankToCreate,
                user = user
            )
        } catch (e: DataIntegrityViolationException) {
            throw BadRequestException("The bank with name ${body.bankToCreate!!.name} already exists for this user")
        }

        val wordToSave = WordDTO(
            origin = body.origin,
            translatedTo = body.translatedTo ?: user.nativeLanguage,
            translatedFrom = body.translatedFrom,
            type = body.type,
            exampleSentences = body.exampleSentences,
            translation = body.translation,
            extraMark = body.extraMark,

            user = userMapper.toDTO(user),
            bank = bankMapper.toDTOOrNull(bank)
        )

        val result = wordService.save(wordMapper.toEntity(wordToSave))

        return ResponseEntity.status(HttpStatus.CREATED).body(wordMapper.toDTO(result));
    }
}