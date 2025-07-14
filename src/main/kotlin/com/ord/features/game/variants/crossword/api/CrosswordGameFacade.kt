package com.ord.features.game.variants.crossword.api

import com.ord.config.GamesConfig
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingCrosswordGameDTO
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.services.GameReviewService
import com.ord.features.game.variants.crossword.ai.CrosswordAIGenerateService
import com.ord.features.game.variants.crossword.dto.api_responses.FinishedCrosswordGameResponse
import com.ord.features.game.variants.crossword.dto.api_responses.StartedCrosswordGameResponse
import com.ord.features.game.variants.shared.api.GameFacadeBase
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableProperAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.ProperAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.computeFinalScore
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

        val reviewedQuestions: Set<IdentifiableProperAnswer> = gameReviewService.reviewUserAnswersAndUpdateDBPoints(
            user = user,
            language = game.language,
            difficulty = game.difficulty,
            expectedAnswers = game.properAnswers.questions,
            userAnswers = body.answers.questions,
        )

        val pointsForQuestions: Int = reviewedQuestions.computeFinalScore(
            moduleRatio = GamesConfig.Points.ScoreFactorsRatio.Crossword.QUESTIONS
        )

        val reviewedFinalAnswer: AnswerScore = AnswerScore.Companion.reviewUserAnswer(
            userAnswer = body.answers.finalWord,
            expectedAnswer = game.properAnswers.finalWord,
            difficulty = game.difficulty
        )

        val pointsForFinalWord: Int = GameReviewService.Companion.computeFinalScoreComponent(
            receivedPoints = reviewedFinalAnswer.wage,
            maxPoints = AnswerScore.CORRECT.wage,
            moduleRatio = GamesConfig.Points.ScoreFactorsRatio.Crossword.FINAL_WORD
        )

        val totalPoints: Int = pointsForQuestions + pointsForFinalWord

        ongoingGameService.completeGame(
            ongoingGame = game,
            totalPoints = totalPoints,
            duration = body.duration
        )

        return ResponseEntity.ok(
            FinishedCrosswordGameResponse(
                totalPoints = totalPoints,
                properFinalWord = ProperAnswer(
                    expectedAnswer = game.properAnswers.finalWord,
                    userAnswer = body.answers.finalWord,
                    score = reviewedFinalAnswer
                ),
                properQuestionsAnswers = reviewedQuestions
            )
        )
    }
}