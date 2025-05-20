package com.backend.ord.features.ongoing_game.ai.generate.dto

import com.backend.ord.features.ongoing_game.model.json.CrosswordProperAnswers
import com.backend.ord.features.ongoing_game.variants.crossword.dto.CrosswordInstruction

typealias GeneratedCrosswordGame = GeneratedGameBase<CrosswordInstruction, CrosswordProperAnswers>

