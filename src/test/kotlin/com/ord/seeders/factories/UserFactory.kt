package com.ord.seeders.factories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.core.user.model.enums.UserRole
import com.ord.seeders.factories.bases.FactoryBase
import com.ord.shared.utils.EnumUtils.getRandomValue
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
