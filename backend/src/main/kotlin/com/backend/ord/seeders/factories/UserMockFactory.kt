package com.backend.ord.seeders.factories

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.model.enums.UserRole
import com.backend.ord.seeders.EnumValuesGenerator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserMockFactory(
    private val passwordEncoder: PasswordEncoder
) : AbstractFactory() {
    fun mockEntity(
        name: String = faker.name().fullName(),
        email: String = faker.internet().emailAddress(),
        password: String = faker.internet().password(),
        role: UserRole = UserRole.USER
    ): UserEntity {
        return UserEntity(
            name = name,
            email = email,
            password = passwordEncoder.encode(password),
            role = role,
            nativeLanguage = EnumValuesGenerator.mockLanguageName()
        )
    }
}
