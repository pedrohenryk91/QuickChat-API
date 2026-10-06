package dev.pedro.quickchat.user;

import dev.pedro.quickchat.storage.StorageService;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import dev.pedro.quickchat.user.dto.CreateUserRequest;
import dev.pedro.quickchat.user.dto.UserResponse;
import dev.pedro.quickchat.user.exception.UserAlreadyExistsException;
import dev.pedro.quickchat.user.exception.UserIdNotFoundException;
import dev.pedro.quickchat.user.exception.UsernameNotFoundException;

@Service
public class UserService {

    private final StorageService storageService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, StorageService storageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.storageService = storageService;
    }

    @Transactional
    public Boolean usernameInUse(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public Boolean validateExists(String id) throws UserIdNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserIdNotFoundException(id);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public User findByIdOrThrow(String id) throws UserIdNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserIdNotFoundException(id));
    }

    @Transactional
    public User findByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public List<User> findAllById(Set<String> ids) {
        return userRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> search(String query, Pageable pageable) {
        if(query == null || query.isBlank()) {
            return Page.empty();
        }

        String cleanedQuery = query.trim();
        var users = userRepository.searchWithUsernamePriority(cleanedQuery, pageable);

        Page<UserResponse> response = users.map(user -> new UserResponse(
            user.getUsername(),
            user.getNickname(),
            user.getIconUrl(),
            user.getId()
        ));

        return response;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) throws UserAlreadyExistsException {
        if(userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }
        String passwordHash = passwordEncoder.encode(request.password());
        String iconUrl = "";

        MultipartFile file = request.file();

        if(file != null && !file.isEmpty()) {
            iconUrl = storageService.uploadFile("image-bucket",file);
        }

        User user = new User(request.username(), request.nickname(), passwordHash, iconUrl);

        userRepository.save(user);

        return new UserResponse(request.username(), request.nickname(), iconUrl, user.getId());
    }

}
