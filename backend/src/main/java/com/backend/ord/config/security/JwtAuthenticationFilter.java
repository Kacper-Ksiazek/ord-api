package com.backend.ord.config.security;

import com.backend.ord.exceptions.JWTTokenIsExpired;
import com.backend.ord.exceptions.NoCorrespondingUserSessionException;
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
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final JwtTokenValidator jwtTokenValidator;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Declare the variables to store the JWT token and the user's email
        String jwtToken;
        String userEmail;
        UserDetails userDetails;

        try {
            // Extract the Authorization cookie from the request
            Optional<String> authCookieValue = CookieUtils.getCookieValue(
                    jwtProperties.getAuthCookieName(),
                    request
            );

            // Check if the cookie value is present
            if (authCookieValue.isEmpty()) {
                // Then continue to the next filter
                filterChain.doFilter(request, response);
                return;
            }

            // Otherwise assign the cookie value to a variable and extract the user's email
            jwtToken = authCookieValue.get();
            userEmail = jwtService.extractUsername(jwtToken);

            // Check if the user is currently authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Check if a user exists in the database
                userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Check if the JWT token is valid
                if (jwtTokenValidator.validate(jwtToken, userDetails)) {
                    // Authenticate the user
                    authenticateUser(userDetails, request);
                }
            }
        }

        // If either the user is not found or there is no corresponding session, delete the cookie
        catch (UsernameNotFoundException | NoCorrespondingUserSessionException e) {
            CookieUtils.deleteCookie(jwtProperties.getAuthCookieName(), response);
        }
        // If the JWT token has expired, TODO: RENEW the session, update the cookie and authenticate the user again
        catch (JWTTokenIsExpired | ExpiredJwtException e) {
            Console.printRed("The JWT token has expired");
        }
        // Eventually, proceed to the next filter
        finally {
            // Continue to the next filter
            filterChain.doFilter(request, response);
        }
    }

    private void authenticateUser(
            String userEmail,
            HttpServletRequest request
    ) {
        // Get the user's details
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        // Authenticate the user
        authenticateUser(userDetails, request);
    }

    private void authenticateUser(
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
