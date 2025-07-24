package com.ord.features.game.variants.sentences_writing.ai

import com.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingGameMapper
import com.ord.features.game.model.ongoing_game.OngoingSentencesWritingGameDTO
import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import com.ord.features.game.services.OngoingGameService
import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameRequest
import com.ord.features.game.variants.shared.ai.AIGameServiceBase
import com.ord.shared.prompts.AvailablePrompts
import com.ord.shared.prompts.Prompt
import org.springframework.stereotype.Service
import java.util.*

@Service
class SentencesWritingAIReviewService(
    val languageProficiencyService: LanguageProficiencyService,
    val ongoingGameService: OngoingGameService,
    val ongoingGameMapper: OngoingGameMapper,
) : AIGameServiceBase() {
    fun review(user: UserEntity, body: FinishSentencesWritingGameRequest) {
        val ongoingGame: OngoingSentencesWritingGameDTO = ongoingGameMapper.toSentencesWritingDTO(
            ongoingGameService.findByIdOrFail(body.gameId, user.id)
        )

        val languageProficiency: LanguageProficiencyEntity =
            languageProficiencyService.findUserProficiencyInLanguageOrThrow(user.id, ongoingGame.language)

        val prompt = Prompt(
            variant = AvailablePrompts.GAMES_REVIEW_SENTENCES_WRITING,
            params = mapOf(
                "language" to ongoingGame.language.toString(),
                "difficulty" to ongoingGame.difficulty.toString(),
                "proficiency" to languageProficiency.proficiency.toString(),
                "serializedAnswers" to serializeUserAnswers(
                    userAnswers = body.answers,
                    instruction = ongoingGame.properAnswers
                ),
            )
        )
    }

    private fun serializeUserAnswers(
        userAnswers: Map<UUID, String>,
        instruction: SentencesWritingProperAnswers
    ): String {
        return instruction.mapIndexed { index, topic ->
            val correspondingUserAnswer: String = userAnswers[topic.id] ?: "❌ no answer provided"

            """
            Question: ${index + 1}.
            - Topic: ${topic.topic}
            - Word: ${topic.word}
            - User's answer: $correspondingUserAnswer
           
            """
        }.joinToString("\n")
    }
}