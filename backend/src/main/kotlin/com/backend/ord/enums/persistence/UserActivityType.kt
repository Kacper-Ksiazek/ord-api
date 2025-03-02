package com.backend.ord.enums.persistence

// TODO: Define how many points each activity generates
enum class UserActivityType {
    CROSSWORD_GAME_COMPLETED_FLAWLESSLY,
    CROSSWORD_GAME_COMPLETED_WITH_MISTAKES,
    GAME_QUIT,

    // TODO: Implement these 2 user activities on adding word
    WORDS_ADDED_IN_ONE_DAY_10,
    WORDS_ADDED_IN_ONE_DAY_25,

    // TODO: Implement these 2 user activities on game gameReviewService
    WORDS_COMPLETED_IN_ONE_WEEK_10,
    WORDS_COMPLETED_IN_ONE_WEEK_25;
}