package com.ord.testing_utils.extensions

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.repositories.WordProgressRepository
import com.ord.core.word.repositories.WordRepository
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer
import com.ord.testing_utils.dto.AlteredWordProperAnswer
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.util.*

fun WordRepository.assertDBPointsWereUpdatedProperly(
    wordProgressRepository: WordProgressRepository,
    words: Set<String>,
    language: LanguageName,
    userId: UUID,
    properAnswers: Set<IdentifiableReviewedWordAnswer>,
    alteredAnswers: Set<AlteredWordProperAnswer> = emptySet(),
) {
    val wordsUsedInGame = findAllWordByTheirOrigins(
        origins = words,
        language = language,
        userId = userId,
    ).collectList().block()!!

    wordsUsedInGame shouldHaveSize words.size

    val progressByWordId = wordProgressRepository
        .findAllByWordIdInAndUserId(wordsUsedInGame.map { it.id!! }.toSet(), userId)
        .collectList()
        .block()!!
        .associateBy { it.wordId }

    properAnswers.forEach {
        val correspondingWordEntity =
            wordsUsedInGame.find { word -> word.sourceWord.lowercase() == it.expectedAnswer.lowercase() }
        val correspondingAlteredAnswer: AlteredWordProperAnswer? = alteredAnswers.find { alteredAnswer ->
            alteredAnswer.questionId == it.id
        }

        val progress = progressByWordId[correspondingWordEntity!!.id!!].shouldNotBeNull()
        progress.points shouldBe maxOf(0, it.score.dbPoints)

        if (correspondingAlteredAnswer != null) {
            correspondingAlteredAnswer.desiredScore shouldBe it.score
        }
    }
}
