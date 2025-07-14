package com.ord.features.game.ai.generate.service.impl

import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.core.word.service.WordService
import com.ord.features.game.ai.generate.dto.GeneratedSentencesWritingGame
import com.ord.features.game.ai.generate.service.AIGenerateGameService
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import org.springframework.stereotype.Service

@Service
@Deprecated("Use dedicated services for each game type instead")
class AIGenerateGameServiceImpl(
    private val wordService: WordService,
    private val openAIAPIClientService: OpenAIAPIClientService,
    private val languageProficiencyService: LanguageProficiencyService,
) : AIGenerateGameService {
    private fun UserEntity.getProficiencyInLanguage(language: LanguageName): LanguageProficiencyEntity {
        return languageProficiencyService
            .findUserProficiencyInLanguageOrThrow(this.id, language)
    }
    override fun generateSentencesWritingGame(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedSentencesWritingGame {
        TODO("Not yet implemented")
    }
}