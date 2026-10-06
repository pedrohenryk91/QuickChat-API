package dev.pedro.quickchat.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pedro.quickchat.shared.dto.JWTUserData;
import dev.pedro.quickchat.user.dto.CreateUserRequest;
import dev.pedro.quickchat.user.dto.UserResponse;
import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("user")
public class UserController implements UserApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal JWTUserData currentUser) {
        var user = userService.findByIdOrThrow(currentUser.userId());
        return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getNickname(), user.getIconUrl(), user.getId()));
    }

    @Override
    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> createUser(@Valid @ModelAttribute CreateUserRequest request) {
        var response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
        @RequestParam String query,
        @PageableDefault(page = 0, size = 20) @ParameterObject Pageable pageable
    ) {
        var response = userService.search(query, pageable);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("check-username")
    public ResponseEntity<Boolean> usernameAlreadyInUse(@RequestParam String query) {
        return ResponseEntity.ok(userService.usernameInUse(query));
    }

}
