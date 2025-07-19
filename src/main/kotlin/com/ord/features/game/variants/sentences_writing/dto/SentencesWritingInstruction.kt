package com.ord.features.game.variants.sentences_writing.dto

import com.ord.features.game.variants.sentences_writing.ai.dto.AIGeneratedSentencesWritingGame
import com.ord.features.game.variants.sentences_writing.dto.helpers.SentencesWritingTopic
import java.util.*

typealias SentencesWritingInstruction = List<SentencesWritingTopic>

fun SentencesWritingInstruction(aiResponse: AIGeneratedSentencesWritingGame): SentencesWritingInstruction {
    return aiResponse.map { (word, topic) ->
        SentencesWritingTopic(
            id = UUID.randomUUID(),
            word = word,
            topic = topic
        )
    }
}
