package com.backend.ord.repositories

import com.backend.ord.domain.infrastructure.CountingSummary
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.enums.persistence.language.LanguageName
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
    // ------
    // READ
    // ------

    @Query("SELECT w.origin FROM Word w WHERE w.translatedFrom = :language ORDER BY w.createdAt DESC")
    fun findNOfLatestWords(language: LanguageName, pageable: Pageable): List<String>

    @Query("SELECT w.origin FROM Word w WHERE w.translatedFrom = :language ORDER BY w.points DESC")
    fun findNOfMostDifficultWords(language: LanguageName, pageable: Pageable): List<String>

    @Query("SELECT w.origin FROM Word w WHERE w.translatedFrom = :language AND w.bank.id IN :banksIds")
    fun findAllWordsFromBanks(language: LanguageName, banksIds: List<UUID>): List<String>

    @Query("SELECT w FROM Word w WHERE w.translatedFrom = :language AND w.origin IN :origins AND w.user.id = :userId")
    fun findAllWordByTheirOrigins(origins: Set<String>, language: LanguageName, userId: UUID): List<Word>

    // ------
    // AGGREGATE
    // ------

    @Query(
        """
        SELECT COUNT(w) FROM Word w 
        WHERE 
            w.translatedFrom = :language 
            AND w.user.id = :userId
            AND date_trunc('day', w.createdAt) = date_trunc('day', CURRENT_DATE)
        """
    )
    fun countWordsCreatedToday(language: LanguageName, userId: UUID): Int

    @Query(
        """
        SELECT COUNT(w) FROM Word w 
        WHERE 
            w.translatedFrom = :language 
            AND w.user.id = :userId 
            AND date_trunc('week', w.createdAt) = date_trunc('week', CURRENT_DATE)
        """
    )
    fun countWordsCreatedInThisWeek(language: LanguageName, userId: UUID): Int

    @Query
        (
        """
        SELECT COUNT(w) FROM Word w 
        WHERE 
            w.translatedFrom = :language 
            AND w.user.id = :userId 
            AND w.isCompleted = true
            AND date_trunc('day', w.completedAt) = date_trunc('day', CURRENT_DATE)
        """
    )
    fun countWordsCompletedToday(language: LanguageName, userId: UUID): Int

    @Query
        (
        """
        SELECT COUNT(w) FROM Word w 
        WHERE
            w.translatedFrom = :language 
            AND w.user.id = :userId 
            AND w.isCompleted = true
            AND date_trunc('week', w.completedAt) = date_trunc('week', CURRENT_DATE)
        """
    )
    fun countWordsCompletedInThisWeek(language: LanguageName, userId: UUID): Int

    @Query(
        value = "SELECT * FROM count_words_by_field('created_at', :language, :userId) cs",
        nativeQuery = true
    )
    fun countCreated(language: LanguageName, userId: UUID): CountingSummary

    // ------
    // UPDATE
    // ------

    @Modifying
    @Query("UPDATE Word w SET w.bank.id = :bankId WHERE w.id = :wordId AND w.user.id = :userId")
    fun changeBankForSingleWord(wordId: UUID, bankId: UUID?, userId: UUID): Int

    @Modifying
    @Query("UPDATE Word w SET w.bank.id = :bankId WHERE w.id IN :wordIds AND w.user.id = :userId")
    fun changeBankForMultipleWords(bankId: UUID?, wordIds: List<UUID>, userId: UUID): Int
}