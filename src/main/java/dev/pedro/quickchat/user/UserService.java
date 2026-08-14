package dev.pedro.quickchat.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.pedro.quickchat.user.dto.CreateUserRequest;
import dev.pedro.quickchat.user.dto.UserResponse;
import dev.pedro.quickchat.user.exception.UserAlreadyExistsException;
import dev.pedro.quickchat.user.exception.UserIdNotFoundException;
import dev.pedro.quickchat.user.exception.UsernameNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Boolean validateExists(String id) throws UserIdNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserIdNotFoundException(id);
        }
        return true;
    }

    @Transactional
    public User findByIdOrThrow(String id) throws UserIdNotFoundException {
        try {
            return userRepository.getReferenceById(id);
        }
        catch (EntityNotFoundException e) {
            throw new UserIdNotFoundException(id);
        }
    }

    @Transactional
    public User findByUsernameOrThrow(String username) {
        try {
            return userRepository.findByUsername(username);
        }
        catch (Exception e) {
            throw new UsernameNotFoundException(username);
        }
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> search(String query, Pageable pageable) {
        if(query == null || query.isBlank()) {
            return Page.empty(pageable);
        }

        String cleanedQuery = query.trim();
        var users = userRepository.searchWithUsernamePriority(cleanedQuery, pageable);

        Page<UserResponse> response = users.map(
            user -> new UserResponse(user.getUsername(), user.getNickname(), user.getIconUrl(), user.getId())
        );

        return response;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) throws UserAlreadyExistsException {
        if(userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }
        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(request.username(), request.nickname(), passwordHash, request.iconUrl());

        userRepository.save(user);

        return new UserResponse(request.username(), request.nickname(), request.iconUrl(), user.getId());
    }

}
