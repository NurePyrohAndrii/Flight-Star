package com.profITsoft.gateway.controller;

import com.profITsoft.gateway.auth.GoogleAuthenticationService;
import com.profITsoft.gateway.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@RestController("oauth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private static final String PREFIX_OAUTH = "/oauth";
    private static final String ENDPOINT_CALLBACK = PREFIX_OAUTH + "/callback";
    public static final String COOKIE_AUTH_STATE = "auth-state";
    public static final String COOKIE_SESSION_ID = "SESSION-ID";

    private final GoogleAuthenticationService googleAuthenticationService;
    private final SessionService sessionService;

    @GetMapping("/authenticate")
    public Mono<Void> authenticate(ServerWebExchange exchange) {
        String state = UUID.randomUUID().toString();
        addStateCookie(exchange, state);
        String redirectUri = buildRedirectUri(exchange.getRequest());
        String authenticationUrl = googleAuthenticationService.generateAuthenticationUrl(redirectUri, state);
        return sendRedirect(exchange, authenticationUrl);
    }

    @GetMapping("/callback")
    public Mono<Void> callback(ServerWebExchange exchange) {
        String code = exchange.getRequest().getQueryParams().getFirst("code");
        String state = exchange.getRequest().getQueryParams().getFirst("state");
        String redirectUri = buildRedirectUri(exchange.getRequest());
        return verifyState(Objects.requireNonNull(state), exchange.getRequest())
                .then(googleAuthenticationService.processAuthenticationCallback(code, redirectUri)
                        .doOnNext(userInfo -> log.info("User authenticated: {}", userInfo))
                        .flatMap(sessionService::saveSession)
                        .flatMap(session -> sessionService.addSessionCookie(exchange, session))
                        .then(sendRedirect(exchange, "/api/profile")));
    }

    private Mono<Void> verifyState(String state, ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getCookies().getFirst(COOKIE_AUTH_STATE))
                .map(HttpCookie::getValue)
                .filter(state::equals)
                .switchIfEmpty(Mono.error(new IllegalStateException("Invalid state")))
                .then();
    }

    private void addStateCookie(ServerWebExchange exchange, String state) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_AUTH_STATE)
                .value(state)
                .path(PREFIX_OAUTH)
                .maxAge(Duration.ofMinutes(30))
                .secure(true)
                .build();
        exchange.getResponse().addCookie(cookie);
    }

    private static Mono<Void> sendRedirect(ServerWebExchange exchange, String location) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().add("Location", location);
        return response.setComplete();
    }

    private String buildRedirectUri(ServerHttpRequest request) {
        return getBaseUrl(request) + ENDPOINT_CALLBACK;
    }

    private String getBaseUrl(ServerHttpRequest request) {
        String uri = request.getURI().toString();
        return uri.substring(0, uri.indexOf(PREFIX_OAUTH));
    }

}
