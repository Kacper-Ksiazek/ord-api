package com.backend.ord.controllers.utils;

import com.backend.ord.domain.dto.UserDTO;
import jakarta.servlet.http.Cookie;

public class MockedAuthenticatedUser {
    private String token;
    private String email;
    private UserDTO userInfo;
    private Cookie authCookie;

    public MockedAuthenticatedUser(String token, String email, UserDTO userInfo, Cookie authCookie) {
        this.token = token;
        this.email = email;
        this.userInfo = userInfo;
        this.authCookie = authCookie;
    }

    public MockedAuthenticatedUser() {
    }

    public static MockedAuthenticatedUserBuilder builder() {
        return new MockedAuthenticatedUserBuilder();
    }

    public String getToken() {
        return this.token;
    }

    public String getEmail() {
        return this.email;
    }

    public UserDTO getUserInfo() {
        return this.userInfo;
    }

    public Cookie getAuthCookie() {
        return this.authCookie;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserInfo(UserDTO userInfo) {
        this.userInfo = userInfo;
    }

    public void setAuthCookie(Cookie authCookie) {
        this.authCookie = authCookie;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MockedAuthenticatedUser)) return false;
        final MockedAuthenticatedUser other = (MockedAuthenticatedUser) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$token = this.getToken();
        final Object other$token = other.getToken();
        if (this$token == null ? other$token != null : !this$token.equals(other$token)) return false;
        final Object this$email = this.getEmail();
        final Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final Object this$userInfo = this.getUserInfo();
        final Object other$userInfo = other.getUserInfo();
        if (this$userInfo == null ? other$userInfo != null : !this$userInfo.equals(other$userInfo)) return false;
        final Object this$authCookie = this.getAuthCookie();
        final Object other$authCookie = other.getAuthCookie();
        if (this$authCookie == null ? other$authCookie != null : !this$authCookie.equals(other$authCookie))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MockedAuthenticatedUser;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $token = this.getToken();
        result = result * PRIME + ($token == null ? 43 : $token.hashCode());
        final Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final Object $userInfo = this.getUserInfo();
        result = result * PRIME + ($userInfo == null ? 43 : $userInfo.hashCode());
        final Object $authCookie = this.getAuthCookie();
        result = result * PRIME + ($authCookie == null ? 43 : $authCookie.hashCode());
        return result;
    }

    public String toString() {
        return "MockedAuthenticatedUser(token=" + this.getToken() + ", email=" + this.getEmail() + ", userInfo=" + this.getUserInfo() + ", authCookie=" + this.getAuthCookie() + ")";
    }

    public static class MockedAuthenticatedUserBuilder {
        private String token;
        private String email;
        private UserDTO userInfo;
        private Cookie authCookie;

        MockedAuthenticatedUserBuilder() {
        }

        public MockedAuthenticatedUserBuilder token(String token) {
            this.token = token;
            return this;
        }

        public MockedAuthenticatedUserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public MockedAuthenticatedUserBuilder userInfo(UserDTO userInfo) {
            this.userInfo = userInfo;
            return this;
        }

        public MockedAuthenticatedUserBuilder authCookie(Cookie authCookie) {
            this.authCookie = authCookie;
            return this;
        }

        public MockedAuthenticatedUser build() {
            return new MockedAuthenticatedUser(this.token, this.email, this.userInfo, this.authCookie);
        }

        public String toString() {
            return "MockedAuthenticatedUser.MockedAuthenticatedUserBuilder(token=" + this.token + ", email=" + this.email + ", userInfo=" + this.userInfo + ", authCookie=" + this.authCookie + ")";
        }
    }
}

