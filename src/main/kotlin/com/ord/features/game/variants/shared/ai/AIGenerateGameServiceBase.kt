package com.ord.features.game.variants.shared.ai

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.core.word.api.requests.enums.GetAllWordsSortOptions
import com.ord.core.word.api.responses.dto.WordListItem
import com.ord.core.word.service.WordService
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.shared.domain.enums.SortDirection
import org.springframework.beans.factory.annotation.Autowired

abstract class AIGenerateGameServiceBase<TGeneratedGame> {
    abstract fun generate(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): TGeneratedGame

    @Autowired
    protected lateinit var wordService: WordService

    @Autowired
    protected lateinit var openAIAPIClientService: OpenAIAPIClientService

    @Autowired
    protected lateinit var languageProficiencyService: LanguageProficiencyService

    protected fun UserEntity.getProficiencyInLanguage(language: LanguageName): LanguageProficiencyEntity {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(this.id, language)
    }

    protected fun getWordsForGame(
        user: UserEntity,
        language: LanguageName,
        n: Int,
        maximumWordLength: Int? = null,
    ): List<WordListItem> {
        // TODO: Generate a list of words using AI - plans for the future far far away

        val words = wordService.findManyWords(
            user = user,
            language = language,
            perPage = 500,
            sortBy = GetAllWordsSortOptions.ORIGIN,
            sortDirection = SortDirection.DESC,
            completed = false
            // TODO: Add more filters
        ).data
            .apply {
                if (size < n) {
                    throw BadRequestException("Not enough words to generate a game")
                }
            }
            .shuffled()

        return if (maximumWordLength != null) {
            words
                .take(2 * n)
                .filter { it.origin.length <= maximumWordLength }
                .take(n)
        } else {
            words.take(n)
        }
    }
}