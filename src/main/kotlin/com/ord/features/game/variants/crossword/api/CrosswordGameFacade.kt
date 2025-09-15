package com.ord.features.game.variants.crossword.api

import com.ord.config.GamesConfig
import com.ord.core.security.UserRepositoryReactive
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingCrosswordGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.services.GameReviewService
import com.ord.features.game.variants.crossword.ai.CrosswordAIGenerateService
import com.ord.features.game.variants.crossword.dto.api_requests.FinishCrosswordGameRequest
import com.ord.features.game.variants.crossword.dto.api_responses.CrosswordReviewedAnswers
import com.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.features.game.variants.shared.api.GameFacadeBase
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.ReviewedWordAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.calculatedWeightedModuleScore
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CrosswordGameFacade(
    private val crosswordAIGenerateGameService: CrosswordAIGenerateService,
    private val userRepositoryReactive: UserRepositoryReactive,
) : GameFacadeBase<
        StartedCrosswordGameResponse,
        FinishCrosswordGameRequest,
        FinishedCrosswordGameResponse>() {
    override fun startGame(
        user: UserEntity,
        body: StartGameRequest
    ): Mono<ResponseEntity<StartedCrosswordGameResponse>> {
        return crosswordAIGenerateGameService.generate(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )
            .flatMap {
                val (instruction, properAnswers) = it

                ongoingGameService.save(
                    OngoingGameEntity(
                        user = user,
                        userId = user.id,
                        type = GameType.CROSSWORD,

                        language = body.language,
                        difficulty = body.difficulty,
                        properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
                    )
                )
                    .map { savedGame ->
                        ResponseEntity.ok(
                            StartedCrosswordGameResponse(
                                gameId = savedGame.id,
                                instruction = instruction,
                                properAnswers = properAnswers
                            )
                        )
                    }
            }
    }

    override fun finishGame(
        user: UserEntity,
        body: FinishCrosswordGameRequest
    ): Mono<ResponseEntity<FinishedCrosswordGameResponse>> {
        return ongoingGameService.findByIdOrFail(id = body.gameId, userId = user.id)
            .map { entity ->
                ongoingGameMapper.toCrosswordDTO(entity)
            }
            .flatMap { game ->
                gameReviewService.reviewUserAnswersAndUpdateDBPoints(
                    user = user,
                    language = game.language,
                    difficulty = game.difficulty,
                    expectedAnswers = game.properAnswers.questions,
                    userAnswers = body.answers.questions,
                )
                    .map { reviewedQuestions ->
                        val scoreForQuestions: Int = reviewedQuestions.calculatedWeightedModuleScore(
                            moduleWeight = GamesConfig.GameScoring.ModulesWeights.Crossword.QUESTIONS,
                            gameMaxScore = GamesConfig.GameScoring.MaxScore.CROSSWORD
                        )

                        val reviewedFinalAnswer: WordAnswerScore = WordAnswerScore.Companion.reviewUserAnswer(
                            userAnswer = body.answers.finalWord,
                            expectedAnswer = game.properAnswers.finalWord,
                            difficulty = game.difficulty
                        )

                        val scoreForFinalWord: Int = GameReviewService.Companion.calculatedWeightedModuleScore(
                            earnedPoints = reviewedFinalAnswer.wage,
                            pointsToEarn = WordAnswerScore.CORRECT.wage,
                            moduleWeight = GamesConfig.GameScoring.ModulesWeights.Crossword.FINAL_WORD,
                            gameMaxScore = GamesConfig.GameScoring.MaxScore.CROSSWORD
                        )

                        val score: Int = scoreForQuestions + scoreForFinalWord

                        Triple(game, reviewedQuestions, score) to reviewedFinalAnswer
                    }
                    .flatMap { (gameData, reviewedFinalAnswer) ->
                        val (game, reviewedQuestions, score) = gameData

                        ongoingGameService.completeGame(
                            ongoingGame = game,
                            score = score,
                            duration = body.duration
                        )
                            .then(Mono.fromCallable {
                                ResponseEntity.ok(
                                    FinishedCrosswordGameResponse(
                                        score = score,
                                        maxScore = GamesConfig.GameScoring.MaxScore.CROSSWORD,
                                        reviewedAnswers = CrosswordReviewedAnswers(
                                            finalWord = ReviewedWordAnswer(
                                                expectedAnswer = game.properAnswers.finalWord,
                                                userAnswer = body.answers.finalWord,
                                                score = reviewedFinalAnswer
                                            ),
                                            questions = reviewedQuestions
                                        )
                                    )
                                )
                            })
                    }
            }
    }
}