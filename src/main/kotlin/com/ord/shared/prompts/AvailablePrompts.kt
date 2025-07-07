package com.ord.shared.prompts

enum class AvailablePrompts(
    val resourcePath: String
) {
    GAMES_GENERATE_CROSSWORD(resourcePath = "games/generate_crossword_game.md"),
    GAMES_GENERATE_WORDS_TYPING(resourcePath = "games/generate_words_typing_game.md"),
    GAMES_GENERATE_SENTENCES_WRITING(resourcePath = "games/generate_sentences_writing_game.md"),
    GAMES_REVIEW_SENTENCES_WRITING(resourcePath = "games/review_sentences_writing_game.md"),

    WORDS_GENERATE_MANUAL(resourcePath = "words/generate_manual.md"),
}