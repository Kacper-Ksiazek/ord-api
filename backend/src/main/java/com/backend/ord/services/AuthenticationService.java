package com.backend.ord.services;

import com.backend.ord.api.requests.AuthenticationRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.api.responses.AuthenticationResponse;
import com.backend.ord.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    AuthenticationResponse register(
            RegisterRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException;

    AuthenticationResponse login(
            AuthenticationRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException;
}
