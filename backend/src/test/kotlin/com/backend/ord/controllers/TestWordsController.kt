package com.backend.ord.controllers

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.request_factories.WordRequestFactory
import com.backend.ord.controllers.utils_for_testing.ControllerTestBase
import com.backend.ord.controllers.utils_for_testing.MockedAuthenticatedUser
import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.domain.entities.Word
import com.backend.ord.repositories.WordRepository
import com.backend.ord.seeders.entities.BankSeeder
import com.backend.ord.services.WordService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class TestWordsController @Autowired constructor(
    mockMvc: MockMvc?,
    objectMapper: ObjectMapper,
    jwtProperties: JwtProperties,
    private val wordService: WordService,
    private val wordRepository: WordRepository,
    private val bankSeeder: BankSeeder,
) : ControllerTestBase(mockMvc!!, objectMapper, jwtProperties) {
    private val BASE_URL = "/api/v1/words/"

    private val wordRequestFactory = WordRequestFactory(
        BASE_URL = BASE_URL,
        objectMapper = objectMapper
    )

    @Test
    fun `Anonymous user cannot create a word`() {
        val request = wordRequestFactory.createWordRequest()

        mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isForbidden()
        )
    }

    @Test
    fun `A word can be created without bank being specified`() {
        val authenticatedUser = mockAuthenticatedUser()

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser
        )

        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated()
        ).andReturn().response

        assertThatThisWordActuallyExists(response, authenticatedUser)
    }


    @Test
    fun `A word can be created and assigned to an existing bank`() {
        val authenticatedUser = mockAuthenticatedUser()

        val bank = bankSeeder.seedOneEntityForUser(authenticatedUser.userInfo)

        val request = wordRequestFactory.createWordRequest(
            authenticatedUser = authenticatedUser,
            bankId = bank.id
        )

        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isCreated()
        ).andReturn().response

        val word: Word = assertThatThisWordActuallyExists(response, authenticatedUser)

        assertEquals(bank.id, word.bank?.id)
    }

    @Test
    fun `A word and a bank can be created at the same time`() {
        //
    }

    @Test
    fun `A word can be create with no extra mark specified`() {
        //
    }

    @Test
    fun `A word cannot be created without example sentences`() {
        //
    }

    @Test
    fun `A word can be created with no translated to language specified defaulting to the user's native language`() {
        //
    }

    @Test
    fun `A word cannot be created with more than 5 example sentences`() {
        //
    }

    @Test
    fun `A word cannot be created with an example sentence that has more than 255 characters`() {
        //
    }

    @Test
    fun `A word cannot be created with bankId and bankToCreate at the same time`() {
        //
    }

    private fun assertThatThisWordActuallyExists(
        response: MockHttpServletResponse,
        authenticatedUser: MockedAuthenticatedUser
    ): Word {
        val responseBody: WordDTO = getResponseBody<WordDTO>(response)
        assertNotNull(responseBody.id)

        assertEquals(authenticatedUser.userInfo.id, responseBody.user.id)

        val valueSavedInDatabase = wordRepository.findByIdOrNull(responseBody.id)

        assertNotNull(valueSavedInDatabase)

        return valueSavedInDatabase!!
    }
}