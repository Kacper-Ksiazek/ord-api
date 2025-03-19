package com.backend.ord.services

import com.backend.ord.controllers.utils_for_testing.bases.ControllerTestBase
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.seeders.entities.LanguageProficiencySeeder
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
class TestLanguageProficiencyService @Autowired constructor(
    objectMapper: ObjectMapper,
    private val languageProficiencyService: LanguageProficiencyService,
    private val languageProficiencySeeder: LanguageProficiencySeeder,
) : ControllerTestBase(objectMapper) {

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
