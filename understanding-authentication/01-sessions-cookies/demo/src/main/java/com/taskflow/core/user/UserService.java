package com.taskflow.core.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int MINIMUM_PASSWORD_LENGTH = 6;

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public void register(String username, String rawPassword) {
        validateCredentials(username, rawPassword);
        ensureUsernameIsAvailable(username);

        User user = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build();
		users.save(user);
	}

    private void validateCredentials(String username, String rawPassword) {
        if (username == null || username.isBlank()
                || rawPassword == null || rawPassword.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new InvalidRegistrationException(
                    "Username required, password must be " + MINIMUM_PASSWORD_LENGTH + "+ characters");
        }
    }

    private void ensureUsernameIsAvailable(String username) {
        if (users.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
    }
}
