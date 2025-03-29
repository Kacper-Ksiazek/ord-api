package com.backend.ord.api.requests.games.data

import com.backend.ord.api.requests.games.CancelGameRequest

data class CancelGameRequestData(
    override val duration: String
) : CancelGameRequest