package com.ord.stubs.ai

import com.ord.core.gpt_tokens_usage.models.GptTokensUsageOperationType

data class AIFixtureEntry(
    val operationKey: String,
    val type: AIFixtureType,
    val resourcePath: String,
    val isDynamic: Boolean = false,
)

class AIFixtureRegistry {
    private val entries: Map<String, AIFixtureEntry> = listOf(
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Conversation.INITIALIZE,
            type = AIFixtureType.STRING_STREAM,
            resourcePath = "stubs/ai/openai/conversation/CONVERSATION_INITIALIZE.stream.json",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Conversation.AI_RESPONSE,
            type = AIFixtureType.STRING_STREAM,
            resourcePath = "stubs/ai/openai/conversation/CONVERSATION_AI_RESPONSE.stream.json",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Conversation.REVIEW_USER_MESSAGE,
            type = AIFixtureType.STRUCTURED,
            resourcePath = "stubs/ai/openai/conversation/CONVERSATION_REVIEW_USER_MESSAGE.json",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Conversation.GENERATE_AI_MESSAGE_LEARNING_TIPS,
            type = AIFixtureType.STRUCTURED,
            resourcePath = "stubs/ai/openai/conversation/CONVERSATION_GENERATE_AI_MESSAGE_LEARNING_TIPS.json",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Conversation.SUGGEST_TOPICS,
            type = AIFixtureType.ARRAY_STREAM,
            resourcePath = "stubs/ai/openai/conversation/CONVERSATION_SUGGEST_TOPICS.stream.json",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Conversation.GENERATE_INTERLOCUTOR,
            type = AIFixtureType.STRUCTURED,
            resourcePath = "stubs/ai/openai/conversation/CONVERSATION_GENERATE_INTERLOCUTOR.json",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.AIExplainer.EXPLAIN_PHRASE,
            type = AIFixtureType.STRING_STREAM,
            resourcePath = "stubs/ai/openai/ai_explainer/AI_EXPLAINER_EXPLAIN_PHRASE.stream.json",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Words.SUGGEST_VOCABULARY,
            type = AIFixtureType.ARRAY_STREAM,
            isDynamic = true,
            resourcePath = "stubs/ai/openai/words/WORDS_SUGGEST_VOCABULARY.stream.json",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Words.GENERATE_MANUAL,
            type = AIFixtureType.STRUCTURED,
            isDynamic = true,
            resourcePath = "",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.QAW.FILL_GAPS,
            type = AIFixtureType.STRUCTURED,
            isDynamic = true,
            resourcePath = "",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Game.Generate.CROSSWORD,
            type = AIFixtureType.STRUCTURED,
            isDynamic = true,
            resourcePath = "",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Game.Generate.WORDS_TYPING,
            type = AIFixtureType.STRUCTURED,
            isDynamic = true,
            resourcePath = "",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Game.Generate.SENTENCES_WRITING,
            type = AIFixtureType.STRUCTURED,
            isDynamic = true,
            resourcePath = "",
        ),
        AIFixtureEntry(
            operationKey = GptTokensUsageOperationType.Game.Review.SENTENCES_WRITING,
            type = AIFixtureType.STRUCTURED,
            isDynamic = true,
            resourcePath = "",
        ),
    ).associateBy { it.operationKey }

    fun get(operationKey: String): AIFixtureEntry =
        entries[operationKey]
            ?: error("No AI fixture registered for operation key: $operationKey")

    fun isRegistered(operationKey: String): Boolean = operationKey in entries
}
