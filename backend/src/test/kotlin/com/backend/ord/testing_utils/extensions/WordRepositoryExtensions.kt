package com.backend.ord.testing_utils.extensions

import com.backend.ord.api.responses.games.utils.IdentifiableProperAnswer
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.repositories.WordRepository
import com.backend.ord.testing_utils.dto.AlteredWordProperAnswer
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.*

fun WordRepository.assertDBPointsWereUpdatedProperly(
    words: Set<String>,
    language: LanguageName,
    userId: UUID,
    properAnswers: Set<IdentifiableProperAnswer>,
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
