package com.backend.ord.services.ai.dto.generated_games

import com.backend.ord.domain.application.games.crossword.CrosswordInstruction
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers

typealias GeneratedCrosswordGame = GeneratedGameBase<CrosswordInstruction, CrosswordProperAnswers>

