package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.UserStatus;
import com.workconnect.api.entity.User;
import com.workconnect.api.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        boolean isEnabled = user.getStatus() == UserStatus.ACTIVE;
        boolean isAccountNonLocked = user.getStatus() != UserStatus.BANNED;

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                isEnabled,
                true,
                true,
                isAccountNonLocked,
                List.of(authority)
        );
    }
}
