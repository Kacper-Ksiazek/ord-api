package com.backend.ord.controllers;

import com.backend.ord.api.requests.LoginRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.config.properties.JwtProperties;
import com.backend.ord.controllers.utils.ControllerTestBase;
import com.backend.ord.controllers.utils.MockedAuthenticatedUser;
import com.backend.ord.domain.dto.UserDTO;
import com.backend.ord.domain.entities.UserSession;
import com.backend.ord.seeders.entities.UserSeeder;
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

class AuthRequestFactory {
    private final String PASSWORD;
    private final String EMAIL;
    private final String BASE_URL;
    private final ObjectMapper objectMapper;

    public AuthRequestFactory(String PASSWORD, String EMAIL, String BASE_URL, ObjectMapper objectMapper) {
        this.PASSWORD = PASSWORD;
        this.EMAIL = EMAIL;
        this.BASE_URL = BASE_URL;
        this.objectMapper = objectMapper;
    }

    /**
     * Create a request to /register
     */
    public MockHttpServletRequestBuilder registerRequest() throws JsonProcessingException {
        return MockMvcRequestBuilders.post(BASE_URL + "/register")
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
    }

    /**
     * Create an authenticated request to /register
     */
    public MockHttpServletRequestBuilder registerRequest(MockedAuthenticatedUser authenticatedUser) throws JsonProcessingException {
        return this.registerRequest().cookie(authenticatedUser.getAuthCookie());
    }

    /**
     * Create a request to /login
     */
    public MockHttpServletRequestBuilder loginRequest() throws JsonProcessingException {
        return MockMvcRequestBuilders.post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(
                        objectMapper.writeValueAsString(
                                new LoginRequest(EMAIL, PASSWORD)
                        )
                );
    }

    /**
     * Create an authenticated request to /login
     */
    public MockHttpServletRequestBuilder loginRequest(MockedAuthenticatedUser authenticatedUser) throws JsonProcessingException {
        return this.loginRequest().cookie(authenticatedUser.getAuthCookie());
    }

    /**
     * Create a request to /logout
     */
    public MockHttpServletRequestBuilder logoutRequest() {
        return MockMvcRequestBuilders.delete(BASE_URL + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    /**
     * Create an authenticated request to /logout
     */
    public MockHttpServletRequestBuilder logoutRequest(MockedAuthenticatedUser authenticatedUser) {
        return this.logoutRequest().cookie(authenticatedUser.getAuthCookie());
    }

    /**
     * Create a request to /me
     */
    public MockHttpServletRequestBuilder meRequest() {
        return MockMvcRequestBuilders.get(BASE_URL + "/me")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    /**
     * Create an authenticated request to /me
     */
    public MockHttpServletRequestBuilder meRequest(MockedAuthenticatedUser authenticatedUser) {
        return this.meRequest().cookie(authenticatedUser.getAuthCookie());
    }
}

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TestAuthController extends ControllerTestBase {
    private final JwtProperties jwtProperties;
    private final UserSessionService userSessionService;
    private final UserService userService;
    private final UserSeeder userSeeder;

    private final String PASSWORD = "123456";
    private final String EMAIL = "test@test.com";
    private final String BASE_URL = "/api/v1/auth";

    private final AuthRequestFactory authRequestFactory = new AuthRequestFactory(PASSWORD, EMAIL, BASE_URL, objectMapper);

    @Autowired
    public TestAuthController(MockMvc mockMvc,
                              ObjectMapper objectMapper,
                              JwtProperties jwtProperties,
                              UserSessionService userSessionService,
                              UserService userService,
                              UserSeeder userSeeder
    ) {
        super(mockMvc, objectMapper, jwtProperties);

        this.userSeeder = userSeeder;
        this.userService = userService;
        this.jwtProperties = jwtProperties;
        this.userSessionService = userSessionService;
    }

    @Test
    public void testControllerTestBaseAuthentication() throws Exception {
        // This method already ensures that cookie is not null
        MockedAuthenticatedUser authenticatedUser = this.mockedAuthenticatedUser();
        assert authenticatedUser != null;

        // Assert a session is created
        assertUserSessionHasBeenCreated(authenticatedUser.getToken(), authenticatedUser.getEmail());

        // Create a request to /me
        MockHttpServletRequestBuilder request = authRequestFactory.meRequest();

        // Without providing the cookie token, /current-user-info should return 403
        mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );

        // But with the cookie token, it should return 200
        MockHttpServletRequestBuilder requestWithCookie = request.cookie(authenticatedUser.getAuthCookie());
        mockMvc.perform(requestWithCookie).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testRegister() throws Exception {
        // Initially, there should be no user with the email in the database
        assert userService.findUserByEmail(EMAIL).isEmpty();

        // Create a request
        MockHttpServletRequestBuilder request = authRequestFactory.registerRequest();

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
        MockHttpServletRequestBuilder request = authRequestFactory.loginRequest();

        // Perform the request
        MockHttpServletResponse response = mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn().getResponse();

        // Perform all kinds of needed assertions
        validateLoginOrRegisterResponse(response);
    }

    @Test
    public void testLogout() throws Exception {
        // First, generate an authenticated user
        MockedAuthenticatedUser authenticatedUser = this.mockedAuthenticatedUser();

        // This ensures also that a cookie is set
        assert authenticatedUser != null;

        // Assert a session is created
        assertUserSessionHasBeenCreated(authenticatedUser.getToken(), authenticatedUser.getEmail());

        // Prepare a request to /logout
        MockHttpServletRequestBuilder request = authRequestFactory.logoutRequest(authenticatedUser);

        // Perform the request
        MockHttpServletResponse response = mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn().getResponse();

        // There should be no session with the token
        assert userSessionService.findByToken(authenticatedUser.getToken()).isEmpty();

        // Auth cookie should have no value
        Cookie authCookie = response.getCookie(jwtProperties.getAuthCookieName());
        assert authCookie != null;
        assert authCookie.getValue().isEmpty();
    }

    @Test
    public void testMe() throws Exception {
        // First, create a user
        MockedAuthenticatedUser authenticatedUser = this.mockedAuthenticatedUser(EMAIL);

        // Create a request
        MockHttpServletRequestBuilder request = authRequestFactory.meRequest(authenticatedUser);

        // Perform the request
        MockHttpServletResponse response = mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn().getResponse();

        // Assert the response data contains information about the user
        assertResponseContainsProperInformationAboutTheUser(response);
    }

    @Test
    public void testRegisterWithExistingEmailShouldReturn400() throws Exception {
        // First, create a user
        userSeeder.insertRowWithCredentials(EMAIL, PASSWORD);

        // Create a request
        MockHttpServletRequestBuilder request = authRequestFactory.registerRequest();

        // Perform the request
        MockHttpServletResponse response = mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        ).andReturn().getResponse();

        // Assert the response body is empty
        validateResponseBodyIsEmpty(response);
    }

    @Test
    public void testRegisterRouteShouldBeAvailableOnlyForAnonymousUsers() throws Exception {
        MockedAuthenticatedUser authenticatedUser = this.mockedAuthenticatedUser();

        // Create a request
        MockHttpServletRequestBuilder request = authRequestFactory.registerRequest(authenticatedUser);

        // Perform the request
        mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        ).andReturn();

    }

    @Test
    public void testLoginRouteShouldBeAvailableOnlyForAnonymousUsers() throws Exception {
        MockedAuthenticatedUser authenticatedUser = this.mockedAuthenticatedUser();

        // Create a request
        MockHttpServletRequestBuilder request = authRequestFactory.loginRequest(authenticatedUser);

        // Perform the request
        mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        ).andReturn();
    }

    @Test
    public void testLoginWithNonExistingEmailShouldReturn400() throws Exception {
        // Create a request
        MockHttpServletRequestBuilder request = authRequestFactory.loginRequest();

        // Perform the request
        MockHttpServletResponse response = mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        ).andReturn().getResponse();

        // Assert the response body is empty
        validateResponseBodyIsEmpty(response);
    }

    @Test
    public void testLogoutRouteShouldBeAvailableOnlyForAuthenticatedUsers() throws Exception {
        // Create a request
        MockHttpServletRequestBuilder request = authRequestFactory.logoutRequest();

        // Perform the request
        mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        ).andReturn();
    }

    @Test
    public void testMeRouteShouldBeAvailableOnlyForAuthenticatedUsers() throws Exception {
        // Create a request
        MockHttpServletRequestBuilder request = authRequestFactory.meRequest();

        // Perform the request
        mockMvc.perform(request).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        ).andReturn();
    }

    private void validateResponseBodyIsEmpty(MockHttpServletResponse response) throws UnsupportedEncodingException {
        // Assert the response body is empty
        assert response.getContentAsString().isEmpty();
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
        val data = getResponseBody(response, new TypeReference<UserDTO>() {
        });
        assert data.getClass().equals(UserDTO.class);
        assert data.getEmail().equals(EMAIL);
    }

    private void assertUserSessionHasBeenCreated(String token) {
        this.assertUserSessionHasBeenCreated(token, EMAIL);
    }

    private void assertUserSessionHasBeenCreated(String token, String email) {
        Optional<UserSession> correspondingSession = userSessionService.findByToken(token);
        assert correspondingSession.isPresent();

        // Assert the user session is valid
        assert correspondingSession.get().getToken().equals(token);

        // Assert the correct user id is associated with the session
        assert correspondingSession.get().getUser().getEmail().equals(email);
    }

    private String assertAuthCookieIsSet(MockHttpServletResponse response) {
        Cookie cookie = response.getCookie(jwtProperties.getAuthCookieName());
        assert cookie != null;

        return cookie.getValue();
    }

}
