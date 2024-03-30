package com.backend.ord.config.security;

import com.backend.ord.domain.entities.User;
import com.backend.ord.exceptions.JWTTokenIsExpired;
import com.backend.ord.exceptions.NoCorrespondingUserSessionException;
import com.backend.ord.exceptions.UserNotFoundException;
import com.backend.ord.services.UserService;
import com.backend.ord.services.UserSessionService;
import com.backend.ord.utils.Console;
import com.backend.ord.utils.CookieUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtFactory jwtFactory;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final JwtTokenValidator jwtTokenValidator;

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final UserSessionService userSessionService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Check if the authentication cookie has been received
        CookieUtils.getCookieValue(
                jwtProperties.getAuthCookieName(),
                request
        ).ifPresent(jwtToken -> {
                    // Authenticate the user using the JWT token
                    handleJwtAuthenticationFilter(
                            jwtToken,
                            request,
                            response
                    );
                }
        );

        // Continue to the next filter
        filterChain.doFilter(request, response);
    }

    private void handleJwtAuthenticationFilter(
            String jwtToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            // Validate the JWT token
            jwtTokenValidator.validateCorrespondingSession(jwtToken);

            // Extract the user's email from the JWT token
            String userEmail = jwtService.extractUsername(jwtToken);

            // Check if the user is currently authenticated
            if (checkIfUserIsNotAuthenticated()) {
                UserDetails userDetails = getUserDetails(jwtToken);

                // Check if the JWT token is valid
                if (jwtTokenValidator.validate(jwtToken, userDetails)) {
                    updateSecurityContext(userDetails, request);
                }
            }
        }
        // If either the user is not found or there is no corresponding session, delete the cookie
        catch (NoCorrespondingUserSessionException | UsernameNotFoundException e) {
            deleteCookieWithToken(response);
        }
        // If the session has expired, renew it
        catch (JWTTokenIsExpired | ExpiredJwtException e) {
            // Get the user associated with the expired JWT token
            userService.findUserByAuthToken(jwtToken).ifPresent(user -> {
                // Generate a new JWT token for the user
                renewUserSession(user, request, response);
                // Remove previous session from the database
                removePreviousSession(jwtToken);
            });


        }
    }

    private void removePreviousSession(String jwtToken) {
        userSessionService.deleteSessionByToken(jwtToken);
    }

    private void renewUserSession(
            User user,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            jwtFactory.createTokenForUser(user, response);

            updateSecurityContext(user.getEmail(), request);
        } catch (UserNotFoundException e) {
            return;
        }
    }

    private void deleteCookieWithToken(HttpServletResponse response) {
        CookieUtils.deleteCookie(jwtProperties.getAuthCookieName(), response);
    }

    private UserDetails getUserDetails(String jwtToken) {
        return userDetailsService.loadUserByUsername(
                jwtService.extractUsername(jwtToken)
        );
    }

    private boolean checkIfUserIsNotAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void updateSecurityContext(
            String userEmail,
            HttpServletRequest request
    ) {
        // Get the user's details
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        // Authenticate the user
        updateSecurityContext(userDetails, request);
    }

    private void updateSecurityContext(
            UserDetails userDetails,
            HttpServletRequest request
    ) {
        // Set the authentication token
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // Set the authentication token's details
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        // Update the SecurityContext with the user's details
        SecurityContextHolder.getContext().setAuthentication(authToken);

    }
}
