package com.ord.features.game.variants.sentences_writing.api

import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.toSentencesWritingDTO
import com.ord.features.game.services.GameReviewService
import com.ord.features.game.variants.sentences_writing.ai.SentencesWritingAIGenerateService
import com.ord.features.game.variants.sentences_writing.ai.SentencesWritingAIReviewService
import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameRequest
import com.ord.features.game.variants.sentences_writing.dto.api_responses.FinishedSentencesWritingGameResponse
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
import com.ord.features.game.variants.shared.api.GameFacadeBase
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class SentencesWritingGameFacade(
    private val sentencesWritingAIReviewService: SentencesWritingAIReviewService,
    private val sentencesWritingAIGenerateService: SentencesWritingAIGenerateService,
) : GameFacadeBase<
        StartedSentencesWritingGameResponse,
        FinishSentencesWritingGameRequest,
        FinishedSentencesWritingGameResponse
        >(
) {
    override fun startGame(
        user: UserEntity,
        body: StartGameRequest
    ): ResponseEntity<StartedSentencesWritingGameResponse> {
        val (instruction, properAnswers) = sentencesWritingAIGenerateService.generate(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        val savedGame = ongoingGameService.save(
            OngoingGameEntity(
                user = user,
                type = GameType.SENTENCES_WRITING,

                language = body.language,
                difficulty = body.difficulty,
                properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
            )
        )

        return ResponseEntity.ok(
            StartedSentencesWritingGameResponse(
                gameId = savedGame.id,
                instruction = instruction,
                properAnswers = properAnswers
            )
        )
    }

    override fun finishGame(
        user: UserEntity,
        body: FinishSentencesWritingGameRequest
    ): ResponseEntity<FinishedSentencesWritingGameResponse> {
        val ongoingGame = ongoingGameService
            .findByIdOrFail(
                id = body.gameId,
                userId = user.id
            )
            .toSentencesWritingDTO(ongoingGameMapper)

        val review = sentencesWritingAIReviewService.review(
            user = user,
            ongoingGame = ongoingGame,
            userAnswers = body.answers
        )

        gameReviewService.updateDBPointsForManyWords(
            user = user,
            language = ongoingGame.language,
            ratedWords = review.reviewedAnswers.associate {
                val accuracy = it.score.toDouble() / it.maxScore.toDouble()

                it.word to WordAnswerScore.fromDouble(accuracy)
            }
        )

        ongoingGameService.completeGame(
            ongoingGame = ongoingGame,
            duration = body.duration,
            score = review.score
        )

        return ResponseEntity.ok(review)
    }
}