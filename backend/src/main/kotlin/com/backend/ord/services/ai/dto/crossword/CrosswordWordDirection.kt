package com.backend.ord.services.ai.dto.crossword

enum class CrosswordWordDirection {
    HORIZONTAL,
    VERTICAL;

    fun opposite(): CrosswordWordDirection {
        return if (this == HORIZONTAL) {
            VERTICAL
        } else {
            HORIZONTAL
        }
    }
}