package com.ord.features.user_activity_log.model.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class UserActivityType(
    val points: Int,
    val frequency: UserActivityFrequency = UserActivityFrequency.NON_PERIODIC
) {
    CROSSWORD_GAME_COMPLETED_FLAWLESSLY(points = 100),
    CROSSWORD_GAME_COMPLETED_WITH_MISTAKES(points = 70),
    WORDS_TYPING_GAME_COMPLETED_FLAWLESSLY(points = 100),
    WORDS_TYPING_GAME_COMPLETED_WITH_MISTAKES(points = 70),
    SENTENCES_WRITING_GAME_COMPLETED_FLAWLESSLY(points = 100),
    SENTENCES_WRITING_GAME_COMPLETED_WITH_MISTAKES(points = 70),
    GAME_QUIT(points = 0),

    WORDS_ADDED_IN_ONE_DAY_10(points = 30, frequency = UserActivityFrequency.DAILY),
    WORDS_ADDED_IN_ONE_WEEK_50(points = 50, frequency = UserActivityFrequency.DAILY),

    WORD_COMPLETED(points = 10),
    WORDS_COMPLETED_IN_ONE_DAY_10(points = 50, frequency = UserActivityFrequency.WEEKLY),
    WORDS_COMPLETED_IN_ONE_WEEK_30(points = 100, frequency = UserActivityFrequency.WEEKLY);
}