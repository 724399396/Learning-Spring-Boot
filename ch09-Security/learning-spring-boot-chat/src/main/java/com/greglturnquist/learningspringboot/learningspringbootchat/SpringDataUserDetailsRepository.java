package com.greglturnquist.learningspringboot.learningspringbootchat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SpringDataUserDetailsRepository implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder().username(
                        user.getUsername())
                        .password(
                                user.getPassword())
                        .authorities(
                                AuthorityUtils.createAuthorityList(user.getRoles())).build()
                );
    }
}
