package com.backend.ord.config

import com.backend.ord.shared.utils.data_classes.Percentage

object GamesConfig {
    /**
     * Configuration of the entire points assignment system.
     */
    object Points {
        const val COMPLETE_WORD_THRESHOLD = 7

        object DatabaseValues {
            const val CORRECT_ANSWER = 2
            const val INCORRECT_ANSWER = -1
            const val HALF_CORRECT_ANSWER = 1
        }

        object ScoreFactorsRatio {

            object Crossword {
                val QUESTIONS = Percentage(75.0)
                val FINAL_WORD = Percentage(25.0)
            }

        }
    }
}