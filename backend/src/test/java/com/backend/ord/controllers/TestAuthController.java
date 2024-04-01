package com.backend.ord.controllers;

import com.backend.ord.api.requests.LoginRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.config.security.JwtProperties;
import com.backend.ord.domain.dto.UserDTO;
import com.backend.ord.domain.entities.UserSession;
import com.backend.ord.seeders.entities.UserSeeder;
import com.backend.ord.seeders.factories.UserMockFactory;
import com.backend.ord.services.UserService;
import com.backend.ord.services.UserSessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TestAuthController {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final UserSessionService userSessionService;
    private final UserService userService;

    private final UserSeeder userSeeder;


    private final String PASSWORD = "123456";
    private final String EMAIL = "test@test.com";
    private final String BASE_URL = "/api/v1/auth";

    @Autowired
    public TestAuthController(MockMvc mockMvc, ObjectMapper objectMapper, JwtProperties jwtProperties, UserMockFactory userMockFactory, UserSessionService userSessionService, UserService userService, UserSeeder userSeeder) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jwtProperties = jwtProperties;
        this.userSessionService = userSessionService;
        this.userService = userService;
        this.userSeeder = userSeeder;
    }

    @Test
    public void testRegister() throws Exception {
        // Initially, there should be no user with the email in the database
        assert userService.findUserByEmail(EMAIL).isEmpty();

        // Create a request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                        objectMapper.writeValueAsString(
                                RegisterRequest.builder()
                                        .name("Test User")
                                        .email(EMAIL)
                                        .password(PASSWORD)
                                        .build()
                        )
                );

        // Perform the request
        MockHttpServletResponse response = mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andReturn().getResponse();

        // Perform all kinds of needed assertions
        validateLoginOrRegisterResponse(response);
    }

    @Test
    public void testLogin() throws Exception {
        // First, create a user
        userSeeder.insertRowWithCredentials(EMAIL, PASSWORD);

        // Create a request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                        objectMapper.writeValueAsString(
                                new LoginRequest(EMAIL, PASSWORD)
                        )
                );

        // Perform the request
        MockHttpServletResponse response = mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn().getResponse();

        // Perform all kinds of needed assertions
        validateLoginOrRegisterResponse(response);
    }

    private void validateLoginOrRegisterResponse(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        // Assert a cookie is set
        String token = assertAuthCookieIsSet(response);

        // Assert a session is created
        assertUserSessionHasBeenCreated(token);

        // Assert the response data contains information about the user
        assertResponseContainsProperInformationAboutTheUser(response);
    }

    private void assertResponseContainsProperInformationAboutTheUser(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        val data = ControllersTestingUtils.getResponseBody(response, new TypeReference<UserDTO>() {
        });
        assert data.getClass().equals(UserDTO.class);
        assert data.getEmail().equals(EMAIL);
    }

    private void assertUserSessionHasBeenCreated(String token) {
        Optional<UserSession> correspondingSession = userSessionService.findByToken(token);
        assert correspondingSession.isPresent();

        // Assert the user session is valid
        assert correspondingSession.get().getToken().equals(token);

        // Assert the correct user id is associated with the session
        assert correspondingSession.get().getUser().getEmail().equals(EMAIL);
    }

    private String assertAuthCookieIsSet(MockHttpServletResponse response) {
        Cookie cookie = response.getCookie(jwtProperties.getAuthCookieName());
        assert cookie != null;

        return cookie.getValue();
    }

}
