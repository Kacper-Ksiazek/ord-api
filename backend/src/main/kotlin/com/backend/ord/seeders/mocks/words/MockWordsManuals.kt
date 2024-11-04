package com.backend.ord.seeders.mocks.words

import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.Word
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.Word.WordType
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.mocks.words.bases.MocksFromJsonFileHandler
import com.backend.ord.seeders.mocks.words.json_data_models.AIGeneratedWordManual
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component

@Component
class MockWordsManuals(
    override val repository: WordRepository
) : MocksFromJsonFileHandler<
        Word,
        List<AIGeneratedWordManual>,
        AIGeneratedWordManual
        > {
    override fun typeReference(): TypeReference<List<AIGeneratedWordManual>> {
        return object : TypeReference<List<AIGeneratedWordManual>>() {}
    }

    override fun parseFileContent(fileContent: List<AIGeneratedWordManual>): List<AIGeneratedWordManual> {
        return fileContent
    }

    override val pathToJSONFile: String = "/words/ai_generated_words_manuals.json"

    override fun convertToEntity(
        jsonData: AIGeneratedWordManual,
        user: User
    ): Word {
        return Word(
            origin = jsonData.originalWord,
            definition = jsonData.definition,
            translation = jsonData.translation,

            translatedTo = LanguageName.POLISH,
            translatedFrom = LanguageName.ENGLISH,

            type = jsonData.type,
            extraMark = null,

            useCases = jsonData.useCases.toSet(),
            exampleSentences = jsonData.exampleSentences.toSet(),

            user = user,
            userId = user.id
        )
    }

}