package com.backend.ord.config.properties;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;

    @Positive
    private long expirationTime;

    @Pattern(regexp = "[a-zA-Z]+(_[a-zA-Z]+)?")
    private String authCookieName;

    @Pattern(regexp = "[a-zA-Z]+(_[a-zA-Z]+)?")
    private String userIdClaimName;

    public String getSecretKey() {
        return this.secretKey;
    }

    public @Positive long getExpirationTime() {
        return this.expirationTime;
    }

    public @Pattern(regexp = "[a-zA-Z]+(_[a-zA-Z]+)?") String getAuthCookieName() {
        return this.authCookieName;
    }

    public @Pattern(regexp = "[a-zA-Z]+(_[a-zA-Z]+)?") String getUserIdClaimName() {
        return this.userIdClaimName;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setExpirationTime(@Positive long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setAuthCookieName(@Pattern(regexp = "[a-zA-Z]+(_[a-zA-Z]+)?") String authCookieName) {
        this.authCookieName = authCookieName;
    }

    public void setUserIdClaimName(@Pattern(regexp = "[a-zA-Z]+(_[a-zA-Z]+)?") String userIdClaimName) {
        this.userIdClaimName = userIdClaimName;
    }
}
