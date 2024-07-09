package com.backend.ord.seeders.factories

import com.backend.ord.domain.entities.*
import com.backend.ord.enums.UserRole
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserMockFactory(private val passwordEncoder: PasswordEncoder) : AbstractFactory() {
    fun mockEntity(): User {
        return User.Companion.builder()
            .name(AbstractFactory.Companion.faker.name().fullName())
            .email(AbstractFactory.Companion.faker.internet().emailAddress())
            .password(passwordEncoder.encode(AbstractFactory.Companion.faker.internet().password()))
            .role(UserRole.USER)
            .build()
    }

    fun mockEntityWithCredentials(email: String?, password: String?): User {
        return User.Companion.builder()
            .name(AbstractFactory.Companion.faker.name().fullName())
            .email(email)
            .password(passwordEncoder.encode(password))
            .role(UserRole.USER)
            .build()
    }
}
