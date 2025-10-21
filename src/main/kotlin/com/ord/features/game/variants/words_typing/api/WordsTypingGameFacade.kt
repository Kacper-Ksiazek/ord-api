package com.ord.features.game.variants.words_typing.api

import com.ord.config.GamesConfig
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.variants.shared.api.GameFacadeBase
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_responses.helpers.calculatedWeightedModuleScore
import com.ord.features.game.variants.words_typing.ai.WordsTypingAIGenerateService
import com.ord.features.game.variants.words_typing.dto.api_requests.FinishWordsTypingGameRequest
import com.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import com.ord.shared.utils.data_classes.Percentage
import io.r2dbc.postgresql.codec.Json
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class WordsTypingGameFacade(
    private val wordsTypingAIGenerateService: WordsTypingAIGenerateService
) : GameFacadeBase<
        StartedWordsTypingGameResponse,
        FinishWordsTypingGameRequest,
        FinishedWordsTypingGameResponse
        >() {
    override fun startGame(
        userId: UUID,
        body: StartGameRequest
    ): Mono<ResponseEntity<StartedWordsTypingGameResponse>> {
        return wordsTypingAIGenerateService
            .generate(
                userId = userId,
                language = body.language,
                difficulty = body.difficulty
            )
            .flatMap { (instruction, properAnswers) ->
                ongoingGameService.save(
                    OngoingGameEntity(
                        userId = userId,
                        type = GameType.WORDS_TYPING,

                        language = body.language,
                        difficulty = body.difficulty,
                        properAnswers = Json.of(jsonObjectMapper.writeValueAsString(properAnswers))
                    )
                )
                    .map { savedGame ->
                        ResponseEntity.ok(
                            StartedWordsTypingGameResponse(
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
        body: FinishWordsTypingGameRequest
    ): Mono<ResponseEntity<FinishedWordsTypingGameResponse>> {
        return ongoingGameService
            .findByIdOrFail(
                id = body.gameId,
                userId = userId
            )
            .map { entity ->
                ongoingGameMapper.toWordsTypingDTO(entity)
            }
            .flatMap { game ->
                gameReviewService
                    .reviewUserAnswersAndUpdateDBPoints(
                        userId = userId,
                        language = game.language,
                        difficulty = game.difficulty,
                        expectedAnswers = game.properAnswers,
                        userAnswers = body.answers
                    )
                    .map { reviewedQuestions ->
                        val score = reviewedQuestions.calculatedWeightedModuleScore(
                            gameMaxScore = GamesConfig.GameScoring.MaxScore.WORDS_TYPING,
                            moduleWeight = Percentage(100.0)
                        )

                        Pair(game, reviewedQuestions) to score
                    }
                    .flatMap { (gameData, score) ->
                        val (game, reviewedQuestions) = gameData

                        ongoingGameService
                            .completeGame(
                                ongoingGame = game,
                                score = score,
                                duration = body.duration
                            )
                            .then(Mono.fromCallable {
                                ResponseEntity.ok(
                                    FinishedWordsTypingGameResponse(
                                        score = score,
                                        maxScore = GamesConfig.GameScoring.MaxScore.WORDS_TYPING,
                                        reviewedAnswers = reviewedQuestions
                                    )
                                )
                            })
                    }
            }
    }
}