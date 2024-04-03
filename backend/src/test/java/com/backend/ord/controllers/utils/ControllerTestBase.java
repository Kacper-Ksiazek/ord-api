package com.backend.ord.controllers.utils;

import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.config.security.JwtProperties;
import com.backend.ord.controllers.utils.MockedAuthenticatedUser;
import com.backend.ord.domain.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.UnsupportedEncodingException;

@AutoConfigureMockMvc
public abstract class ControllerTestBase {
    protected final MockMvc mockMvc;
    protected final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;

    protected ControllerTestBase(MockMvc mockMvc, ObjectMapper objectMapper, JwtProperties jwtProperties) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jwtProperties = jwtProperties;
    }

    public MockedAuthenticatedUser mockedAuthenticatedUser() throws Exception {
        final String AUTH_USER_EMAIL_ADDRESS = "random.authenticated.email@gmail.com";
        return mockedAuthenticatedUser(AUTH_USER_EMAIL_ADDRESS);
    }

    public MockedAuthenticatedUser mockedAuthenticatedUser(String email) throws Exception {
        // Create a request
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                        objectMapper.writeValueAsString(
                                RegisterRequest.builder()
                                        .name("Test User")
                                        .email(email)
                                        .password("qwerty123")
                                        .build()
                        )
                );

        // Perform the request
        MockHttpServletResponse response = mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andReturn().getResponse();

        // Parse the response
        Cookie authCookie = response.getCookie(jwtProperties.getAuthCookieName());
        assert authCookie != null;

        UserDTO userInfo = objectMapper.readValue(response.getContentAsString(), UserDTO.class);

        return MockedAuthenticatedUser.builder()
                .token(authCookie.getValue())
                .userInfo(userInfo)
                .authCookie(authCookie)
                .email(email)
                .build();

    }

    public <T> T getResponseBody(MvcResult result, TypeReference<T> responseType) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                responseType
        );
    }

    public <T> T getResponseBody(MockHttpServletResponse response, TypeReference<T> responseType) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(
                response.getContentAsString(),
                responseType
        );
    }
}
