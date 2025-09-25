package com.ord.seeders.factories

/*

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.features.bank.model.BankEntity
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.factories.bases.FactoryBase
import com.ord.shared.utils.EnumUtils.getRandomValue
import com.ord.shared.utils.EnumUtils.getRandomValueOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class WordFactory(
    private val userSeeder: UserSeeder,
) : FactoryBase() {
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
        points: Int = 0,
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
        user: UserEntity = userSeeder.seedOneEntity(),
        bank: BankEntity? = null
    ): WordEntity {
        return WordEntity(
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

 */
