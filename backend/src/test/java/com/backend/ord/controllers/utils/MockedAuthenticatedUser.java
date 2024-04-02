package com.backend.ord.controllers.utils;

import com.backend.ord.domain.dto.UserDTO;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockedAuthenticatedUser {
    private String token;
    private String email;
    private UserDTO userInfo;
    private Cookie authCookie;
}

