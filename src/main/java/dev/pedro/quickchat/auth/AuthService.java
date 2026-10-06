package dev.pedro.quickchat.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.pedro.quickchat.user.UserService;

@Service
public class AuthService implements UserDetailsService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findByUsernameOrThrow(username);
    }

}
