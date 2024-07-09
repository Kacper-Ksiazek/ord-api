package com.backend.ord.controllers

import com.backend.ord.config.properties.JwtProperties
import com.backend.ord.controllers.utils.ControllerTestBase
import com.backend.ord.domain.entities.User
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class TestUserController @Autowired protected constructor(
    mockMvc: MockMvc,
    objectMapper: ObjectMapper,
    jwtProperties: JwtProperties
) : ControllerTestBase(mockMvc, objectMapper, jwtProperties) {
    @Test
    @Throws(Exception::class)
    fun testGetAllUserShouldReturnACollectionOfUsers() {
        // Create a request
        val request = MockMvcRequestBuilders.get(BASE_URL + "/")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)

        // Perform the request
        val response = mockMvc.perform(request).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andReturn()


        val parsed: List<User> = getResponseBody(response)

        for (user in parsed) {
            AssertionsForClassTypes.assertThat(user.id).isNotNull()
        }
    }

    companion object {
        private const val BASE_URL = "/users"
    }
}
