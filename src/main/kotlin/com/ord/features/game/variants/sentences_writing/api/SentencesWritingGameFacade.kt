package com.ord.features.game.variants.sentences_writing.api

import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.toSentencesWritingDTO
import com.ord.features.game.variants.sentences_writing.ai.SentencesWritingAIGenerateService
import com.ord.features.game.variants.sentences_writing.ai.SentencesWritingAIReviewService
import com.ord.features.game.variants.sentences_writing.dto.api_requests.FinishSentencesWritingGameRequest
import com.ord.features.game.variants.sentences_writing.dto.api_responses.FinishedSentencesWritingGameResponse
import com.ord.features.game.variants.sentences_writing.dto.api_responses.StartedSentencesWritingGameResponse
import com.ord.features.game.variants.shared.api.GameFacadeBase
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

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
        userId: UUID,
        body: StartGameRequest
    ): Mono<ResponseEntity<StartedSentencesWritingGameResponse>> {
        return sentencesWritingAIGenerateService
            .generate(
                userId = userId,
                language = body.language,
                difficulty = body.difficulty
            )
            .flatMap { (instruction, properAnswers) ->
                ongoingGameService.save(
                    OngoingGameEntity(
                        userId = userId,
                        type = GameType.SENTENCES_WRITING,

                        language = body.language,
                        difficulty = body.difficulty,
                        properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
                    )
                )
                    .map { savedGame ->
                        ResponseEntity.ok(
                            StartedSentencesWritingGameResponse(
                                gameId = savedGame.id!!,
                                instruction = instruction,
                                properAnswers = properAnswers
                            )
                        )
                    }
            }
    }

    override fun finishGame(
        userId: UUID,
        body: FinishSentencesWritingGameRequest
    ): Mono<ResponseEntity<FinishedSentencesWritingGameResponse>> {
        return ongoingGameService.findByIdOrFail(
            id = body.gameId,
            userId = userId,
        )
            .map { entity ->
                entity.toSentencesWritingDTO(ongoingGameMapper)
            }
            .flatMap { ongoingGame ->
                sentencesWritingAIReviewService
                    .review(
                        userId = userId,
                        ongoingGame = ongoingGame,
                        userAnswers = body.answers
                    )
                    .flatMap { review ->
                        gameReviewService
                            .updateDBPointsForManyWords(
                                userId = userId,
                                language = ongoingGame.language,
                                ratedWords = review.reviewedAnswers.associate {
                                    val accuracy = it.score.toDouble() / it.maxScore.toDouble()

                                    it.word to WordAnswerScore.fromDouble(accuracy)
                                }
                            )
                            .then(
                                Mono.defer {
                                    ongoingGameService.completeGame(
                                        ongoingGame = ongoingGame,
                                        duration = body.duration,
                                        score = review.score
                                    )
                                })
                            .then(Mono.fromCallable {
                                ResponseEntity.ok(review)
                            })
                    }
            }
    }
}