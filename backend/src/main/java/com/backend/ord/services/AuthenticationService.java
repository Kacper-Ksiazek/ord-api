package com.backend.ord.services;

import com.backend.ord.api.requests.LoginRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.domain.entities.User;
import com.backend.ord.exceptions.ForbiddenException;
import com.backend.ord.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    User register(
            RegisterRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException;

    User login(
            LoginRequest request,
            HttpServletResponse response
    ) throws UserNotFoundException;

    void logout(HttpServletRequest request, HttpServletResponse response) throws ForbiddenException;
}
