package com.ord.config

import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.shared.utils.data_classes.Percentage

object GamesConfig {
    /**
     * Configuration of the entire points assignment system.
     */
    object WordPoints {
        const val COMPLETE_WORD_THRESHOLD = 7

        object DatabaseValues {
            const val CORRECT_ANSWER = 2
            const val INCORRECT_ANSWER = -1
            const val HALF_CORRECT_ANSWER = 1
        }
    }

    object GameScoring {
        object MaxScore {
            // All points here should be divisible by 100
            const val CROSSWORD = 1000
            const val WORDS_TYPING = 600
            const val SENTENCES_WRITING = 1300

            fun getMaxScoreForGameType(gameType: GameType): Int {
                return when (gameType) {
                    GameType.CROSSWORD -> CROSSWORD
                    GameType.WORDS_TYPING -> WORDS_TYPING
                    GameType.SENTENCES_WRITING -> SENTENCES_WRITING

                    else -> throw IllegalArgumentException("Unknown game type: $gameType")
                }
            }
        }

        object ModulesWeights {
            object Crossword {
                val QUESTIONS = Percentage(75.0)
                val FINAL_WORD = Percentage(25.0)
            }

            /**
             * Ratio of each evaluation criteria for each sentence
             */
            object SentencesWriting {
                val ANSWER_LENGTH = Percentage(35.0)
                val VOCABULARY = Percentage(50.0)
                val CORRECT_WORD_USAGE = Percentage(15.0)
            }
        }
    }
}