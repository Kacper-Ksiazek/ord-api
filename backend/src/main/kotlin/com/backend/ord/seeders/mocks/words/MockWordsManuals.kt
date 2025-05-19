package com.backend.ord.seeders.mocks.words

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.word.model.WordEntity
import com.backend.ord.core.word.repository.WordRepository
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.seeders.mocks.bases.MocksFromJsonFileHandler
import com.backend.ord.seeders.mocks.words.json_data_models.AIGeneratedWordManualInJSON
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class MockWordsManuals(
    override val repository: WordRepository
) : MocksFromJsonFileHandler<
        WordEntity,
        List<AIGeneratedWordManualInJSON>,
        AIGeneratedWordManualInJSON
        > {
    private lateinit var availableBanks: List<Bank>

    override val jsonFileContentTypeRef: TypeReference<List<AIGeneratedWordManualInJSON>> =
        object : TypeReference<List<AIGeneratedWordManualInJSON>>() {}

    override val pathToJsonFile: String = "mocks/words/ai_generated_words_manuals.json"

    override fun convertToEntity(
        jsonData: AIGeneratedWordManualInJSON,
        user: UserEntity
    ): WordEntity {
        val bank: Bank? = getRandomBank()

        return WordEntity(
            origin = jsonData.originalWord,
            definition = jsonData.definition,
            translation = jsonData.translation,

            translatedTo = LanguageName.POLISH,
            translatedFrom = LanguageName.ENGLISH,

            type = jsonData.type,
            extraMark = jsonData.extraMark,

            useCases = jsonData.useCases.toSet(),
            exampleSentences = jsonData.exampleSentences.toSet(),

            isBookmarked = Random.nextBoolean(),

            user = user,
            userId = user.id,

            bank = bank,
            bankId = bank?.id
        )
    }

    fun seedFromJSONFile(
        user: UserEntity,
        banks: List<Bank>
    ): List<WordEntity> {
        this.availableBanks = banks

        return seedFromJSONFile(user)
    }

    fun getRandomBank(
        likelihoodOfReturningNull: Int = 25
    ): Bank? {
        return if (Random.nextInt(100) < likelihoodOfReturningNull) {
            null
        } else {
            availableBanks.random()
        }
    }
}