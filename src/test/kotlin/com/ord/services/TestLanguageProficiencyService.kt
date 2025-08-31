package com.ord.services

import com.ord.config.properties.JwtProperties
import com.ord.controllers.bases.ControllerTestBase
import com.ord.core.langugae_proficiency.LanguageProficiencyRepository
import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.service.LanguageProficiencyService
import com.ord.core.user.model.UserMapper
import com.ord.seeders.entities.LanguageProficiencySeeder
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
class TestLanguageProficiencyService @Autowired constructor(
    private val languageProficiencyService: LanguageProficiencyService,
    private val languageProficiencySeeder: LanguageProficiencySeeder,

    objectMapper: ObjectMapper,
    mockMvc: MockMvc,
    jwtProperties: JwtProperties,
    languageProficiencyRepository: LanguageProficiencyRepository,
    userMapper: UserMapper,
    userRepository: UserRepository
) : ControllerTestBase(
    objectMapper = objectMapper,
    mockMvc = mockMvc,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
    userMapper = userMapper,
    userRepository = userRepository
) {

    @Test
    fun `Fetch user proficiency in given language`() {
        val authenticatedUser = this.mockAuthenticatedUser()

        languageProficiencySeeder.seedOneEntity(
            user = userMapper.toEntity(authenticatedUser.userInfo),
            languageName = LanguageName.ENGLISH,
        )

        val proficiency = languageProficiencyService.findUserProficiencyInLanguage(
            authenticatedUser.userInfo.id,
            LanguageName.ENGLISH
        )

        assertNotNull(proficiency)
    }
}
