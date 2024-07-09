package com.backend.ord.exceptions

import lombok.NoArgsConstructor

@NoArgsConstructor
class NoCorrespondingUserSessionException(message: String?) : Exception(message)
