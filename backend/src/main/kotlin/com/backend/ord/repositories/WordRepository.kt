package com.backend.ord.repositories

import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WordRepository :
    UserResourceRepository<Word>,
    WordRepositoryCustomMethods {
    @Query("SELECT w.origin FROM Word w WHERE w.translatedFrom = :language ORDER BY w.createdAt DESC")
    fun findNOfLatestWords(language: LanguageName, pageable: Pageable): List<String>

    @Query("SELECT w.origin FROM Word w WHERE w.translatedFrom = :language ORDER BY w.points DESC")
    fun findNOfMostDifficultWords(language: LanguageName, pageable: Pageable): List<String>

    @Query("SELECT w.origin FROM Word w WHERE w.translatedFrom = :language AND w.bank.id IN :banksIds")
    fun findAllWordsFromBanks(language: LanguageName, banksIds: List<UUID>): List<String>

    @Query("SELECT w FROM Word w WHERE w.translatedFrom = :language AND w.origin IN :origins AND w.user.id = :userId")
    fun findAllWordByTheirOrigins(origins: Set<String>, language: LanguageName, userId: UUID): List<Word>

    @Modifying
    @Query("UPDATE Word w SET w.bank.id = :bankId WHERE w.id = :wordId AND w.user.id = :userId")
    fun changeBankForSingleWord(wordId: UUID, bankId: UUID?, userId: UUID): Int

    @Modifying
    @Query("UPDATE Word w SET w.bank.id = :bankId WHERE w.id IN :wordIds AND w.user.id = :userId")
    fun changeBankForMultipleWords(bankId: UUID?, wordIds: List<UUID>, userId: UUID): Int
}