package com.backend.ord.repositories;

import com.backend.ord.domain.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    Optional<UserSession> findByToken(String token);

    @Query("SELECT us FROM UserSession us WHERE us.token = :token AND us.user.id = :userId")
    Optional<UserSession> findByTokenAndUserId(String token, UUID userId);
}
