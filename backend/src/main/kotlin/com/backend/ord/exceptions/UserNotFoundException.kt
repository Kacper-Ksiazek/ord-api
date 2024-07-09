package com.backend.ord.exceptions

import lombok.NoArgsConstructor
import java.util.*

@NoArgsConstructor
class UserNotFoundException(userId: UUID?) : Exception("User with id $userId not found")
