package com.ord.features.game.variants.words_typing.dto.api_responses

import com.ord.features.game.variants.shared.dto.api_responses.FinishedGameResponse
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer

typealias FinishedWordsTypingGameResponse = FinishedGameResponse<Set<IdentifiableReviewedWordAnswer>>
