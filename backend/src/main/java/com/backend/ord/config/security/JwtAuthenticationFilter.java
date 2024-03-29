package com.backend.ord.config.security;

import com.backend.ord.domain.entities.UserSession;
import com.backend.ord.utils.Console;
import com.backend.ord.utils.CookieUtils;
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
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
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
            String jwtToken = authCookieValue.get();
            String userEmail = jwtService.extractUsername(jwtToken);

            // Check if the user is currently authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Check if a user exists in the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                UUID userId = jwtService.extractUserId(jwtToken);
                Console.printRed("User ID: " + userId);

                // Check if the JWT token is valid
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    // Create an authentication token
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
        }
        catch (UsernameNotFoundException e) {
            // If the user is not found, remove the cookie containing the corrupted JWT token
            CookieUtils.deleteCookie(jwtProperties.getAuthCookieName(), response);
        }
        finally {
            // Continue to the next filter
            filterChain.doFilter(request, response);
        }

        // TODO: Catch NoCorrespondingSessionException and remove the cookie containing the corrupted JWT token
    }
}
