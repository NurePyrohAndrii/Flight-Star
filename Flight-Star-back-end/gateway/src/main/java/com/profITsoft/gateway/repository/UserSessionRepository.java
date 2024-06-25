package com.profITsoft.gateway.repository;

import com.profITsoft.gateway.auth.dto.UserInfo;
import com.profITsoft.gateway.data.UserSession;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface UserSessionRepository {

    Mono<UserSession> createSession(UserInfo userInfo, Instant expiresAt);

    Mono<UserSession> findById(String id);
}
