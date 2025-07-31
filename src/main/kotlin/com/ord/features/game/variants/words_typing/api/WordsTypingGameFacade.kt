package com.ord.features.game.variants.words_typing.api

import com.ord.config.GamesConfig
import com.ord.core.user.model.UserEntity
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.OngoingWordsTypingGameDTO
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.variants.words_typing.dto.api_requests.FinishWordsTypingGameRequest
import com.ord.features.game.variants.shared.api.GameFacadeBase
import com.ord.features.game.variants.shared.dto.api_requests.StartGameRequest
import com.ord.features.game.variants.shared.dto.api_responses.helpers.calculatedWeightedModuleScore
import com.ord.features.game.variants.words_typing.ai.WordsTypingAIGenerateService
import com.ord.features.game.variants.words_typing.dto.api_responses.FinishedWordsTypingGameResponse
import com.ord.features.game.variants.words_typing.dto.api_responses.StartedWordsTypingGameResponse
import com.ord.shared.utils.data_classes.Percentage
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class WordsTypingGameFacade(
    private val wordsTypingAIGenerateService: WordsTypingAIGenerateService
) : GameFacadeBase<
        StartedWordsTypingGameResponse,
        FinishWordsTypingGameRequest,
        FinishedWordsTypingGameResponse
        >() {
    override fun startGame(
        user: UserEntity,
        body: StartGameRequest
    ): ResponseEntity<StartedWordsTypingGameResponse> {
        val (instruction, properAnswers) = wordsTypingAIGenerateService.generate(
            user = user,
            language = body.language,
            difficulty = body.difficulty
        )

        val savedGame: OngoingGameEntity = ongoingGameService.save(
            OngoingGameEntity(
                user = user,
                type = GameType.WORDS_TYPING,

                language = body.language,
                difficulty = body.difficulty,
                properAnswers = jsonObjectMapper.writeValueAsString(properAnswers)
            )
        )

        return ResponseEntity.ok(
            StartedWordsTypingGameResponse(
                gameId = savedGame.id,
                instruction = instruction,
                properAnswers = properAnswers
            )
        )
    }

    override fun finishGame(
        user: UserEntity,
        body: FinishWordsTypingGameRequest
    ): ResponseEntity<FinishedWordsTypingGameResponse> {
        val game: OngoingWordsTypingGameDTO = ongoingGameMapper.toWordsTypingDTO(
            entity = ongoingGameService.findByIdOrFail(id = body.gameId, userId = user.id)
        )

        val reviewedQuestions = gameReviewService.reviewUserAnswersAndUpdateDBPoints(
            user = user,
            language = game.language,
            difficulty = game.difficulty,
            expectedAnswers = game.properAnswers,
            userAnswers = body.answers
        )

        val score = reviewedQuestions.calculatedWeightedModuleScore(
            gameMaxScore = GamesConfig.GameScoring.MaxScore.WORDS_TYPING,
            moduleWeight = Percentage(100.0)
        )

        ongoingGameService.completeGame(
            ongoingGame = game,
            score = score,
            duration = body.duration
        )

        return ResponseEntity.ok(
            FinishedWordsTypingGameResponse(
                score = score,
                maxScore = GamesConfig.GameScoring.MaxScore.WORDS_TYPING,
                reviewedAnswers = reviewedQuestions
            )
        )
    }
}