package com.backend.ord.services;

import com.backend.ord.api.requests.AuthenticationRequest;
import com.backend.ord.api.requests.RegisterRequest;
import com.backend.ord.api.responses.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse login(AuthenticationRequest request);
}
