package com.backend.ord.controllers

import com.backend.ord.api.requests.words.CreateWordRequest
import com.backend.ord.config.security.JwtService
import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.WordMapper
import com.backend.ord.services.WordService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/words")
class WordController(
    private val jwtService: JwtService,
//    // TODO: Implement bank service
//    private val bankService: BankService,
    private val wordMapper: WordMapper,
    private val userMapper: UserMapper,
    private val wordService: WordService
) {
    @GetMapping("/")
    fun getAllWords(
        request: HttpServletRequest
    ): ResponseEntity<List<WordDTO>> {
        val user = jwtService.getAuthenticatedUser(request)!!

        val words: List<WordDTO> = wordMapper.toDTOList(
            wordService.findAllForUser(user.id)
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
        @RequestBody body: CreateWordRequest
    ): ResponseEntity<WordDTO> {
        val user = jwtService.getAuthenticatedUser(request)!!

        // TODO: Implement bank service
        // val bank =

        val wordToSave = WordDTO(
            origin = body.origin,
            user = userMapper.toDTO(user),
            translatedTo = body.translatedTo ?: user.nativeLanguage,
            translatedFrom = body.translatedFrom,
            type = body.type,
            exampleSentences = body.exampleSentences,
            translation = body.translation,
        )

        // TODO: Use bank repository to save the word
        val result = wordService.save(wordMapper.toEntity(wordToSave))

        return ResponseEntity.status(HttpStatus.CREATED).body(wordMapper.toDTO(result));
    }
}