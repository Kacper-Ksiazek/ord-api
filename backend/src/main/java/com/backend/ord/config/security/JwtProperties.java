package com.backend.ord.config.security;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secretKey;

    @Positive
    private long expirationTime;

    @Pattern(regexp = "[a-zA-Z]+(_[a-zA-Z]+)?")
    private String authCookieName;

    @Pattern(regexp = "[a-zA-Z]+(_[a-zA-Z]+)?")
    private String userIdClaimName;
}
