package com.ord.features.game.variants.crossword.dto.helpers.board

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
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