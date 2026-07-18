package com.ord.testing_utils.mocks.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.gpt_tokens_usage.models.GptTokensUsageOperationType
import com.ord.core.word.api.ai.responses.openai.OpenAIGeneratedWordManual
import com.ord.core.word.api.ai.responses.openai.OpenAIGrammar
import com.ord.core.word.api.ai.responses.openai.OpenAIPronunciation
import com.ord.core.word.models.word.enums.WordType
import com.ord.features.game.variants.crossword.ai.dto.openai.OpenAICrossword
import com.ord.features.game.variants.crossword.ai.dto.openai.OpenAICrosswordQuestion
import com.ord.features.game.variants.sentences_writing.ai.dto.generate.openai.OpenAISentencesWritingGame
import com.ord.features.game.variants.sentences_writing.ai.dto.generate.openai.OpenAIWordTopic
import com.ord.features.game.variants.sentences_writing.ai.dto.review.openai.OpenAIReviewedSingleTopic
import com.ord.features.game.variants.sentences_writing.ai.dto.review.openai.OpenAIScoringCriteria
import com.ord.features.game.variants.sentences_writing.ai.dto.review.openai.OpenAISentencesWritingEvaluationCriteria
import com.ord.features.game.variants.sentences_writing.ai.dto.review.openai.OpenAISentencesWritingReview
import com.ord.features.game.variants.words_typing.ai.dto.openai.OpenAIWordPair
import com.ord.features.game.variants.words_typing.ai.dto.openai.OpenAIWordsTyping
import com.ord.features.quickly_added_words.api.ai.responses.openai.OpenAIQAWFillGapsBatch
import com.ord.features.quickly_added_words.api.ai.responses.openai.OpenAIQAWFillGapsItem
class AIFixtureDynamicBuilder {
    fun buildStructured(
        operationKey: String,
        prompt: String,
        typeReference: TypeReference<*>,
    ): Any? {
        if (!AIFixtureRegistryDynamicKeys.isDynamic(operationKey)) return null

        return when (operationKey) {
            GptTokensUsageOperationType.Words.GENERATE_MANUAL ->
                buildWordManual(prompt)

            GptTokensUsageOperationType.QAW.FILL_GAPS ->
                buildQAWFillGaps(prompt)

            GptTokensUsageOperationType.Game.Generate.CROSSWORD ->
                buildCrossword(prompt)

            GptTokensUsageOperationType.Game.Generate.WORDS_TYPING ->
                buildWordsTyping(prompt)

            GptTokensUsageOperationType.Game.Generate.SENTENCES_WRITING ->
                buildSentencesWriting(prompt)

            GptTokensUsageOperationType.Game.Review.SENTENCES_WRITING ->
                buildSentencesWritingReview(prompt)

            else -> null
        }
    }

    private fun buildWordManual(prompt: String): OpenAIGeneratedWordManual {
        val word = AIPromptParsingUtils.parseTargetWord(prompt)
            ?: error("Could not parse target word from prompt for WORDS_GENERATE_MANUAL")

        return OpenAIGeneratedWordManual(
            word = word,
            translation = "translation of $word",
            definition = "A clear definition of the word \"$word\" suitable for language learners.",
            type = WordType.NOUN,
            extraMark = "",
            useCases = listOf("Use $word in everyday conversation."),
            exampleSentences = listOf(
                com.ord.core.word.api.ai.responses.openai.OpenAIExampleSentence(
                    context = "General usage",
                    sentence = "Example sentence with $word.",
                    translation = "Example translation.",
                )
            ),
            collocations = emptyList(),
            pronunciation = OpenAIPronunciation(ipa = "", syllables = "", stress = 0),
            grammar = OpenAIGrammar(
                gender = "",
                definiteArticle = "",
                pluralForm = "",
                comparativeForm = "",
                superlativeForm = "",
                irregularForms = emptyMap(),
                conjugations = emptyList(),
            ),
            synonyms = listOf("synonym"),
            antonyms = listOf("antonym"),
            commonMistakes = listOf("Avoid common spelling mistakes."),
            culturalNotes = "",
            learningTips = "Practice using $word in short sentences.",
        )
    }

    private fun buildQAWFillGaps(prompt: String): OpenAIQAWFillGapsBatch {
        val words = AIPromptParsingUtils.parseNumberedWords(prompt)
        require(words.isNotEmpty()) { "Could not parse input words from QAW fill-gaps prompt" }

        return OpenAIQAWFillGapsBatch(
            items = words.map { inputWord ->
                OpenAIQAWFillGapsItem(
                    inputWord = inputWord,
                    word = inputWord,
                    translation = "translation of $inputWord",
                    definition = "A concise definition of \"$inputWord\" for vocabulary practice.",
                    type = resolveWordType(inputWord).name,
                    extraMark = "",
                    error = "",
                )
            }
        )
    }

    private fun buildCrossword(prompt: String): OpenAICrossword {
        val words = AIPromptParsingUtils.parseBulletWords(prompt)
        val amount = AIPromptParsingUtils.parseAmountOfQuestions(prompt) ?: words.size
        val selectedWords = words.take(amount)

        require(selectedWords.size == amount) {
            "Crossword stub needs at least $amount words in prompt, found ${selectedWords.size}"
        }

        return OpenAICrossword(
            answer = "PERSEVERANCE",
            answerExplanation = "The act of continuing despite difficulty.",
            questions = selectedWords.mapIndexed { index, word ->
                OpenAICrosswordQuestion(
                    word = word,
                    clue = "Clue for word ${index + 1}: $word",
                )
            },
        )
    }

    private fun buildWordsTyping(prompt: String): OpenAIWordsTyping {
        val words = AIPromptParsingUtils.parseBulletWords(prompt)
        require(words.isNotEmpty()) { "Could not parse words from words typing prompt" }

        return OpenAIWordsTyping(
            wordPairs = words.map { word ->
                OpenAIWordPair(
                    word = word,
                    clue = "Hint for $word",
                )
            }
        )
    }

    private fun buildSentencesWriting(prompt: String): OpenAISentencesWritingGame {
        val words = AIPromptParsingUtils.parseBulletWords(prompt)
        require(words.isNotEmpty()) { "Could not parse words from sentences writing prompt" }

        return OpenAISentencesWritingGame(
            wordTopics = words.map { word ->
                OpenAIWordTopic(
                    word = word,
                    topic = "Write a sentence about $word in a real-life context.",
                )
            }
        )
    }

    private fun buildSentencesWritingReview(prompt: String): OpenAISentencesWritingReview {
        val topicIds = AIPromptParsingUtils.parseTopicIds(prompt)
        require(topicIds.isNotEmpty()) { "Could not parse topic IDs from sentences writing review prompt" }

        return OpenAISentencesWritingReview(
            reviews = topicIds.map { topicId ->
                OpenAIReviewedSingleTopic(
                    topicId = topicId,
                    evaluationCriteria = OpenAISentencesWritingEvaluationCriteria(
                        fitsTopic = true,
                        vocabulary = OpenAIScoringCriteria(score = 8, comment = "Good vocabulary range."),
                        answerLength = OpenAIScoringCriteria(score = 8, comment = "Sufficient length."),
                        correctWordUsage = OpenAIScoringCriteria(score = 8, comment = "Word used correctly."),
                    ),
                    suggestedCorrectAnswer = "",
                )
            }
        )
    }

    private fun resolveWordType(word: String): WordType = when (word.lowercase()) {
        "verbose" -> WordType.ADJECTIVE
        "meeting", "library" -> WordType.NOUN
        else -> WordType.NOUN
    }
}

private object AIFixtureRegistryDynamicKeys {
    private val dynamicKeys = setOf(
        GptTokensUsageOperationType.Words.GENERATE_MANUAL,
        GptTokensUsageOperationType.QAW.FILL_GAPS,
        GptTokensUsageOperationType.Game.Generate.CROSSWORD,
        GptTokensUsageOperationType.Game.Generate.WORDS_TYPING,
        GptTokensUsageOperationType.Game.Generate.SENTENCES_WRITING,
        GptTokensUsageOperationType.Game.Review.SENTENCES_WRITING,
    )

    fun isDynamic(operationKey: String): Boolean = operationKey in dynamicKeys
}
