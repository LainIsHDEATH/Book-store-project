package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.repo.auth.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final AuthUserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = authUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.isBlocked()) {
            throw new UsernameNotFoundException("Account is blocked");
        }
        return new User(user.getEmail(), user.getPassword(),
                java.util.List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }
}
