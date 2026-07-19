package com.ord.seeders.factories

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.seeders.factories.bases.FactoryBase
import com.ord.shared.utils.EnumUtils.getRandomValue
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserFactory(
    private val passwordEncoder: PasswordEncoder
) : FactoryBase() {
    fun mockEntity(
        name: String = faker.name().fullName(),
        email: String = "test-${UUID.randomUUID()}@example.com",
        password: String = faker.internet().password(),
    ): UserEntity {
        return UserEntity(
            name = name,
            email = email,
            nativeLanguage = LanguageName::class.getRandomValue(),
            selectedLearningLanguage = LanguageName::class.getRandomValue()
        )
    }
}
