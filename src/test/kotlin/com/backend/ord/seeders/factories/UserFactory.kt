package com.backend.ord.seeders.factories

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.model.enums.UserRole
import com.backend.ord.seeders.factories.bases.FactoryBase
import com.backend.ord.shared.utils.EnumUtils.getRandomValue
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserFactory(
    private val passwordEncoder: PasswordEncoder
) : FactoryBase() {
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
            nativeLanguage = LanguageName::class.getRandomValue(),
        )
    }
}
