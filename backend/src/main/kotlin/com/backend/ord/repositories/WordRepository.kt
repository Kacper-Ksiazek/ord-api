package com.backend.ord.repositories

import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.repositories.bases.UserResourceRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WordRepository : UserResourceRepository<Word> {
    @Query("SELECT w FROM Word w WHERE w.user.id = :userId AND (lower(w.origin) LIKE lower(concat('%', :phrase, '%')) OR lower(w.translation) LIKE lower(concat('%', :phrase, '%')))")
    fun findAllForUserBySearchingPhrase(userId: UUID, phrase: String): List<Word>

    @Query("SELECT w.origin FROM Word w WHERE w.translatedFrom = :language ORDER BY w.createdAt DESC")
    fun findNOfLatestWords(language: LanguageName, pageable: Pageable): List<String>

    @Query("SELECT w.origin FROM Word w WHERE w.translatedFrom = :language ORDER BY w.points DESC")
    fun findNOfMostDifficultWords(language: LanguageName, pageable: Pageable): List<String>

    @Query("SELECT w.origin FROM Word w WHERE w.translatedFrom = :language AND w.bank.id IN :banksIds")
    fun findAllWordsFromBanks(language: LanguageName, banksIds: List<UUID>): List<String>

    @Modifying
    @Query("UPDATE Word w SET w.bank.id = :bankId WHERE w.id = :wordId AND w.user.id = :userId")
    fun changeBankForSingleWord(wordId: UUID, bankId: UUID?, userId: UUID): Int

    @Modifying
    @Query("UPDATE Word w SET w.bank.id = :bankId WHERE w.id IN :wordIds AND w.user.id = :userId")
    fun changeBankForMultipleWords(bankId: UUID?, wordIds: List<UUID>, userId: UUID): Int
}