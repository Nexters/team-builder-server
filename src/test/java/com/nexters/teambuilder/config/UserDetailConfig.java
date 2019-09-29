package com.nexters.teambuilder.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class UserDetailConfig {
    @Bean
    @Profile("userDetailService")
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                com.nexters.teambuilder.user.domain.User user = new com.nexters.teambuilder.user.domain.User("originman", "password1212", "kiwon",
                        13, com.nexters.teambuilder.user.domain.User.Role.ROLE_ADMIN, com.nexters.teambuilder.user.domain.User.Position.DEVELOPER, "originman@nexters.com");
                return user;
            }
        };
    }
}
