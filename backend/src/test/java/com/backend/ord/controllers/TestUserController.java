package com.backend.ord.controllers;

import com.backend.ord.config.security.JwtProperties;
import com.backend.ord.controllers.utils.ControllerTestBase;
import com.backend.ord.domain.entities.User;
import com.backend.ord.utils.Console;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TestUserController extends ControllerTestBase {
    private static final String BASE_URL = "/users";

    @Autowired
    protected TestUserController(MockMvc mockMvc, ObjectMapper objectMapper, JwtProperties jwtProperties) {
        super(mockMvc, objectMapper, jwtProperties);
    }

    @Test
    void testGetAllUserShouldReturnACollectionOfUsers() throws Exception {
        // Create a request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Perform the request
        MvcResult response = mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        List<User> parsed = getResponseBody(response, new TypeReference<List<User>>() {});

        for (User user : parsed) {
            assertThat(user.getId()).isNotNull();
        }
    }
}
