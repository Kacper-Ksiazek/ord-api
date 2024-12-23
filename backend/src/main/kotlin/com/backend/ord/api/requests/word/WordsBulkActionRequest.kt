package com.backend.ord.api.requests.word

import java.util.UUID

interface WordsBulkActionRequest {
    val ids: List<UUID>
}