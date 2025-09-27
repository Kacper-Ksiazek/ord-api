package com.ord.seeders.factories

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.model.WordEntity
import com.ord.core.word.model.enums.WordExtraMark
import com.ord.core.word.model.enums.WordType
import com.ord.core.word.model.json.ExampleSentence
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.factories.bases.FactoryBase
import com.ord.shared.utils.EnumUtils.getRandomValue
import com.ord.shared.utils.EnumUtils.getRandomValueOrNull
import io.r2dbc.postgresql.codec.Json
import org.springframework.stereotype.Component
import java.util.*

@Component
class WordFactory(
    private val userSeeder: UserSeeder,
) : FactoryBase() {
    private val objectMapper = jacksonObjectMapper()

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
        userId: UUID? = null,
        bankId: UUID? = null,
        bankGroupId: UUID? = null,
    ): WordEntity {
        val userIdToUse = userId ?: userSeeder.seedOneEntity().id!!

        return WordEntity(
            origin = origin,
            translation = translation,
            definition = definition,
            useCases = Json.of(objectMapper.writeValueAsString(useCases)),
            isBookmarked = isBookmarked,
            points = points,
            type = type,
            extraMark = extraMark,
            translatedFrom = translatedFrom,
            translatedTo = translatedTo,
            exampleSentences = Json.of(objectMapper.writeValueAsString(exampleSentences)),
            userId = userIdToUse,
            bankId = bankId,
            bankGroupId = bankGroupId,
        )
    }
}
