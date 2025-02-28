package com.backend.ord.services

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.request_factories.AuthRequestFactory
import com.backend.ord.controllers.utils_for_testing.bases.ControllerTestBase
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.repositories.LanguageProficiencyRepository
import com.backend.ord.seeders.entities.LanguageProficiencySeeder
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
    mockMvc: MockMvc?,
    objectMapper: ObjectMapper,
    private val jwtProperties: JwtProperties,
    private val userService: UserService,
    private val languageProficiencyService: LanguageProficiencyService,
    private val languageProficiencySeeder: LanguageProficiencySeeder,
    private val userMapper: UserMapper,
    private val languageProficiencyRepository: LanguageProficiencyRepository
) : ControllerTestBase(
    mockMvc = mockMvc!!,
    objectMapper = objectMapper,

    userMapper = userMapper,
    jwtProperties = jwtProperties,
    languageProficiencyRepository = languageProficiencyRepository,
) {
    private val PASSWORD = "123456"
    private val EMAIL = "test@test.com"
    private val BASE_URL = "/api/v1/auth"

    private val authRequestFactory = AuthRequestFactory(PASSWORD, EMAIL, BASE_URL, objectMapper);
    //

    @Test
    fun `Fetch user proficiency in given language`() {
        val authenticatedUser = this.mockAuthenticatedUser()

        val languageProficiency = languageProficiencySeeder.seedOneEntity(
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
