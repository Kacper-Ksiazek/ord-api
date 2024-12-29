package com.backend.ord.seeders.factories

import com.backend.ord.domain.persistence.embedded.ExampleSentence
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType
import com.backend.ord.seeders.entities.UserSeeder
import com.backend.ord.utils.EnumUtils.getRandomValue
import com.backend.ord.utils.EnumUtils.getRandomValueOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class WordMockFactory(
    private val userSeeder: UserSeeder,
) : AbstractFactory() {
    fun mockEntity(
        origin: String = UUID.randomUUID().toString(),
        translation: String = faker.name().title(),
        definition: String = faker.name().title(),
        useCases: Set<String> = mutableSetOf<String>().apply {
            repeat(times = faker.number().numberBetween(1, 5)) {
                add("$it-${faker.name().title()}")
            }
        },
        isBookmarked: Boolean = faker.bool().bool(),
        points: Int = faker.number().numberBetween(1, 100),
        type: WordType = WordType::class.getRandomValue(),
        extraMark: WordExtraMark? = WordExtraMark::class.getRandomValueOrNull(changesForNull = 75),
        translatedFrom: LanguageName = LanguageName::class.getRandomValue(),
        translatedTo: LanguageName = LanguageName::class.getRandomValue(),
        exampleSentences: Set<ExampleSentence> = mutableSetOf<ExampleSentence>().apply {
            repeat(times = faker.number().numberBetween(1, 5)) {
                add(
                    ExampleSentence(
                        sentence = it.toString() + "-" + faker.name().title(),
                        translation = faker.name().title()
                    )
                )
            }
        },
        user: User = userSeeder.seedOneEntity(),
        bank: Bank? = null
    ): Word {
        return Word(
            origin = origin,
            translation = translation,
            definition = definition,
            useCases = useCases,
            isBookmarked = isBookmarked,
            points = points,
            type = type,
            extraMark = extraMark,
            translatedFrom = translatedFrom,
            translatedTo = translatedTo,
            exampleSentences = exampleSentences,
            user = user,
            userId = user.id,
            bank = bank,
            bankId = bank?.id,
            bankGroupId = bank?.bankGroupId
        )
    }
}