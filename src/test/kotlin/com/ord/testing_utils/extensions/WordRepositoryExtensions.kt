package com.ord.testing_utils.extensions

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.repository.WordRepository
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer
import com.ord.testing_utils.dto.AlteredWordProperAnswer
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.*

fun WordRepository.assertDBPointsWereUpdatedProperly(
    words: Set<String>,
    language: LanguageName,
    userId: UUID,
    properAnswers: Set<IdentifiableReviewedWordAnswer>,
    alteredAnswers: Set<AlteredWordProperAnswer> = emptySet()
) {
    val wordsUsedInGame = this.findAllWordByTheirOrigins(
        origins = words,
        language = language,
        userId = userId,
    )

    wordsUsedInGame shouldHaveSize words.size

    properAnswers.forEach {
        val correspondingWordEntity =
            wordsUsedInGame.find { word -> word.origin.lowercase() == it.expectedAnswer.lowercase() }
        val correspondingAlteredAnswer: AlteredWordProperAnswer? = alteredAnswers.find { alteredAnswer ->
            alteredAnswer.questionId == it.id
        }

        correspondingWordEntity!!.points shouldBe it.score.dbPoints

        if (correspondingAlteredAnswer != null) {
            correspondingAlteredAnswer.desiredScore shouldBe it.score
        }
    }
}
