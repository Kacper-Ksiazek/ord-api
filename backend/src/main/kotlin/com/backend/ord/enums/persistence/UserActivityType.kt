package com.backend.ord.enums.persistence

import com.backend.ord.enums.application.UserActivityFrequency

enum class UserActivityType(
    val points: Int,
    val frequency: UserActivityFrequency = UserActivityFrequency.NON_PERIODIC
) {
    CROSSWORD_GAME_COMPLETED_FLAWLESSLY(points = 100),
    CROSSWORD_GAME_COMPLETED_WITH_MISTAKES(points = 70),
    GAME_QUIT(points = 0),

    // TODO: Implement these 2 user activities on adding word
    WORDS_ADDED_IN_ONE_DAY_10(points = 20, frequency = UserActivityFrequency.DAILY),
    WORDS_ADDED_IN_ONE_DAY_25(points = 30, frequency = UserActivityFrequency.DAILY),

    // TODO: Implement these 2 user activities on game gameReviewService
    WORDS_COMPLETED_IN_ONE_WEEK_10(points = 35, frequency = UserActivityFrequency.WEEKLY),
    WORDS_COMPLETED_IN_ONE_WEEK_25(points = 50, frequency = UserActivityFrequency.WEEKLY)
}