package com.backend.ord.domain.entities;

import com.backend.ord.domain.entities.abstracts.EntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "user_sessions")
public class UserSession extends EntityBase {
    @Column(name = "token", nullable = false, updatable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public UserSession(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public UserSession() {
    }

    public static UserSessionBuilder builder() {
        return new UserSessionBuilder();
    }

    public String getToken() {
        return this.token;
    }

    public User getUser() {
        return this.user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class UserSessionBuilder {
        private String token;
        private User user;

        UserSessionBuilder() {
        }

        public UserSessionBuilder token(String token) {
            this.token = token;
            return this;
        }

        public UserSessionBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserSession build() {
            return new UserSession(this.token, this.user);
        }

        public String toString() {
            return "UserSession.UserSessionBuilder(token=" + this.token + ", user=" + this.user + ")";
        }
    }
}
