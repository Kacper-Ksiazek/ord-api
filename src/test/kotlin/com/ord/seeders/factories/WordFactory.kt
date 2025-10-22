package com.ord.seeders.factories

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.word.models.word.WordEntity
import com.ord.core.word.models.word.enums.WordExtraMark
import com.ord.core.word.models.word.enums.WordType
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
    private val objectMapper = jacksonObjectMapper()

    fun mockEntity(
        origin: String = UUID.randomUUID().toString(),
        translation: String = faker.name().title(),
        definition: String = faker.name().title(),
        isBookmarked: Boolean = faker.bool().bool(),
        points: Int = 0,
        type: WordType = WordType::class.getRandomValue(),
        extraMark: WordExtraMark? = WordExtraMark::class.getRandomValueOrNull(changesForNull = 75),
        translatedFrom: LanguageName = LanguageName::class.getRandomValue(),
        translatedTo: LanguageName = LanguageName::class.getRandomValue(),
        userId: UUID? = null,
        bankId: UUID? = null,
        bankGroupId: UUID? = null,
    ): WordEntity {
        val userIdToUse = userId ?: userSeeder.seedOneEntity().id!!

        return WordEntity(
            origin = origin,
            translation = translation,
            definition = definition,
            isBookmarked = isBookmarked,
            points = points,
            type = type,
            extraMark = extraMark,
            translatedFrom = translatedFrom,
            translatedTo = translatedTo,
            userId = userIdToUse,
            bankId = bankId,
            bankGroupId = bankGroupId,
        )
    }
}
