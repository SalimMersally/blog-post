package com.taskflow.infra.user;

import com.taskflow.core.user.User;
import com.taskflow.core.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository users;

    @Override
    public User save(User user) {
        return toDomain(users.save(toEntity(user)));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.existsByUsername(username);
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .passwordHash(user.getPasswordHash())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .passwordHash(entity.getPasswordHash())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
