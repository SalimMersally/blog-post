package com.taskflow.api;

import com.taskflow.core.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService users;
    private final AuthenticationManager authenticationManager;
    private final SessionAuthenticationStrategy sessionStrategy;
    private final SecurityContextRepository contextRepository;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public CurrentUser signup(@RequestBody Credentials credentials,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        users.register(credentials.getUsername(), credentials.getPassword());
        return authenticate(credentials, request, response);
    }

    @PostMapping("/login")
    public CurrentUser login(@RequestBody Credentials credentials,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        return authenticate(credentials, request, response);
    }

    @GetMapping("/me")
    public CurrentUser me(Authentication authentication) {
        return currentUser(authentication);
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    private CurrentUser authenticate(Credentials credentials,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken
                .unauthenticated(credentials.getUsername(), credentials.getPassword());
        Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);

        sessionStrategy.onAuthentication(authenticationResult, request, response);
        saveSecurityContext(authenticationResult, request, response);
        return currentUser(authenticationResult);
    }

    private void saveSecurityContext(Authentication authentication,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        contextRepository.saveContext(context, request, response);
    }

    private CurrentUser currentUser(Authentication authentication) {
        return CurrentUser.builder()
                .username(authentication.getName())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Credentials {
        private String username;
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentUser {
        private String username;
    }
}
