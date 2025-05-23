package com.backend.ord.features.game.ai.generate.dto

import com.backend.ord.features.game.model.json.CrosswordProperAnswers
import com.backend.ord.features.game.variants.crossword.dto.CrosswordInstruction

typealias GeneratedCrosswordGame = GeneratedGameBase<CrosswordInstruction, CrosswordProperAnswers>

