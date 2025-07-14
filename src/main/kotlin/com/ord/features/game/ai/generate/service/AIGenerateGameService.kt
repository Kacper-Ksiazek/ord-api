package com.ord.features.game.ai.generate.service

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.ai.generate.dto.GeneratedSentencesWritingGame
import com.ord.features.game.variants.words_typing.ai.dto.GeneratedWordsTypingGame
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty

@Deprecated("Use dedicated services for each game type instead")
interface AIGenerateGameService {
    fun generateSentencesWritingGame(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedSentencesWritingGame
}