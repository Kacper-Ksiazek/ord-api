package com.ord.shared.prompts

import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate
import com.ord.shared.prompts.structured_outputs.features.conversation.aiMessageLearningTipsSchema
import com.ord.shared.prompts.structured_outputs.features.conversation.generatedAIInterlocutorSchema
import com.ord.shared.prompts.structured_outputs.features.conversation.reviewedUserConversationMessageSchema
import com.ord.shared.prompts.structured_outputs.features.games.crosswordGenerateSchema
import com.ord.shared.prompts.structured_outputs.features.games.sentencesWritingGenerateSchema
import com.ord.shared.prompts.structured_outputs.features.games.sentencesWritingReviewSchema
import com.ord.shared.prompts.structured_outputs.features.games.wordsTypingGenerateSchema
import com.ord.shared.prompts.structured_outputs.features.words.generatedWordManualSchema
import com.ord.shared.prompts.structured_outputs.features.words.wordFillGapsSchema

enum class AvailablePrompts(
    val resourcePath: String,
    val structuredOutput: StructuredOutputTemplate? = null,
) {
    CONVERSATION_SUGGEST_TOPIC(resourcePath = "conversation/suggest_conversation_topic.md"),
    CONVERSATION_GENERATE_AI_INTERLOCUTOR(
        resourcePath = "conversation/generate_ai_interlocutor.md",
        structuredOutput = generatedAIInterlocutorSchema
    ),

    CONVERSATION_REQUEST_AI_RESPONSE(resourcePath = "conversation/respond_in_conversation.md"),
    CONVERSATION_INITIALIZE(resourcePath = "conversation/initialize_conversation.md"),
    CONVERSATION_REVIEW_USER_RESPONSE(
        resourcePath = "conversation/review_user_message_in_conversation.md",
        structuredOutput = reviewedUserConversationMessageSchema
    ),
    CONVERSATION_GENERATE_AI_MESSAGE_LEARNING_TIPS(
        resourcePath = "conversation/generate_ai_message_learning_tips.md",
        structuredOutput = aiMessageLearningTipsSchema
    ),

    GAMES_GENERATE_CROSSWORD(
        resourcePath = "games/generate_crossword_game.md",
        structuredOutput = crosswordGenerateSchema
    ),
    GAMES_GENERATE_WORDS_TYPING(
        resourcePath = "games/generate_words_typing_game.md",
        structuredOutput = wordsTypingGenerateSchema
    ),
    GAMES_GENERATE_SENTENCES_WRITING(
        resourcePath = "games/generate_sentences_writing_game.md",
        structuredOutput = sentencesWritingGenerateSchema
    ),
    GAMES_REVIEW_SENTENCES_WRITING(
        resourcePath = "games/review_sentences_writing_game.md",
        structuredOutput = sentencesWritingReviewSchema
    ),

    WORDS_GENERATE_MANUAL(
        resourcePath = "words/generate_word_manual.md",
        structuredOutput = generatedWordManualSchema
    ),
    WORDS_SUGGEST_VOCABULARY(resourcePath = "words/suggest_vocabulary.md"),
    WORDS_EXPLAIN(resourcePath = "words/explain_word.md"),

    AI_EXPLAINER_FOLLOW_UP_SIMPLER(resourcePath = "ai_explainer/follow_up_simpler.md"),
    AI_EXPLAINER_FOLLOW_UP_MORE_EXAMPLES(resourcePath = "ai_explainer/follow_up_more_examples.md"),
    AI_EXPLAINER_FOLLOW_UP_REGISTER(resourcePath = "ai_explainer/follow_up_register.md"),
    AI_EXPLAINER_FOLLOW_UP_SIMILAR_EXPRESSIONS(resourcePath = "ai_explainer/follow_up_similar_expressions.md"),
    AI_EXPLAINER_FOLLOW_UP_IN_THIS_CONTEXT(resourcePath = "ai_explainer/follow_up_in_this_context.md"),

    WORDS_FILL_GAPS(
        resourcePath = "words/fill_gaps.md",
        structuredOutput = wordFillGapsSchema,
    ),
}