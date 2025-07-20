package com.ord.features.game.variants.crossword.api

import com.ord.config.GamesConfig
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingCrosswordGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.services.GameReviewService
import com.ord.features.game.variants.crossword.ai.CrosswordAIGenerateService
import com.ord.features.game.variants.crossword.dto.api_responses.CrosswordReviewedAnswers
import com.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.features.game.variants.shared.api.GameFacadeBase
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.ReviewedWordAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.calculatedWeightedModuleScore
import com.ord.features.game.variants.shared.enums.AnswerScore
import com.ord.features.game.variants.words_typing.dto.api_requests.FinishCrosswordGameRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class CrosswordGameFacade(
    private val crosswordAIGenerateGameService: CrosswordAIGenerateService,
) : GameFacadeBase<
        StartedCrosswordGameResponse,
        FinishCrosswordGameRequest,
        FinishedCrosswordGameResponse>() {
    override fun startGame(
        user: UserEntity,
        body: StartGameRequest
    ): ResponseEntity<StartedCrosswordGameResponse> {
        val (instruction, properAnswers) = crosswordAIGenerateGameService.generate(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        val savedGame: OngoingGameEntity = ongoingGameService.save(
            OngoingGameEntity(
                user = user,
                type = GameType.CROSSWORD,

                language = body.language,
                difficulty = body.difficulty,
                properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
            )
        )

        return ResponseEntity.ok(
            StartedCrosswordGameResponse(
                gameId = savedGame.id,
                instruction = instruction,
                properAnswers = properAnswers
            )
        )
    }

    override fun finishGame(
        user: UserEntity,
        body: FinishCrosswordGameRequest
    ): ResponseEntity<FinishedCrosswordGameResponse> {
        val game: OngoingCrosswordGameDTO = ongoingGameMapper.toCrosswordDTO(
            entity = ongoingGameService.findByIdOrFail(id = body.gameId, userId = user.id)
        )

        val reviewedQuestions: Set<IdentifiableReviewedWordAnswer> =
            gameReviewService.reviewUserAnswersAndUpdateDBPoints(
                user = user,
                language = game.language,
                difficulty = game.difficulty,
                expectedAnswers = game.properAnswers.questions,
                userAnswers = body.answers.questions,
            )

        val scoreForQuestions: Int = reviewedQuestions.calculatedWeightedModuleScore(
            moduleWeight = GamesConfig.GameScoring.ModulesWeights.Crossword.QUESTIONS,
            gameMaxScore = GamesConfig.GameScoring.MaxScore.CROSSWORD
        )

        val reviewedFinalAnswer: AnswerScore = AnswerScore.Companion.reviewUserAnswer(
            userAnswer = body.answers.finalWord,
            expectedAnswer = game.properAnswers.finalWord,
            difficulty = game.difficulty
        )

        val scoreForFinalWord: Int = GameReviewService.Companion.calculatedWeightedModuleScore(
            earnedPoints = reviewedFinalAnswer.wage,
            pointsToEarn = AnswerScore.CORRECT.wage,
            moduleWeight = GamesConfig.GameScoring.ModulesWeights.Crossword.FINAL_WORD,
            gameMaxScore = GamesConfig.GameScoring.MaxScore.CROSSWORD
        )

        val score: Int = scoreForQuestions + scoreForFinalWord

        ongoingGameService.completeGame(
            ongoingGame = game,
            score = score,
            duration = body.duration
        )

        return ResponseEntity.ok(
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
    }
}