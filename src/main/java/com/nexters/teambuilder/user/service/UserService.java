package com.nexters.teambuilder.user.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.nexters.teambuilder.config.security.InValidTokenException;
import com.nexters.teambuilder.config.security.TokenService;
import com.nexters.teambuilder.user.api.dto.UserRequest;
import com.nexters.teambuilder.user.domain.User;
import com.nexters.teambuilder.user.domain.UserRepository;
import com.nexters.teambuilder.user.exception.LoginErrorException;
import com.nexters.teambuilder.user.exception.PasswordNotMatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encryptor = new BCryptPasswordEncoder();

    public User createUser(UserRequest request) {

        return userRepository.save(User.builder()
                .id(request.getId())
                .password(encryptor.encode(request.getPassword()))
                .name(request.getName())
                .term(request.getTerm())
                .role(request.getRole())
                .position(request.getPosition())
                .build());
    }

    public User findByToken(String token) {
        return Optional.of(tokenService.verify(token))
                .map(map -> {
                    System.out.println(map.get("uuid"));
                    return map.get("uuid");
                })
                .flatMap(userRepository::findUserByUuid)
                .orElseThrow(() -> new InValidTokenException());
    }

    public Map<String, String> logIn(String id, String password) {
        return userRepository.findUserById(id)
                .map(user -> {
                    user.setAuthenticated(true);
                    if(!encryptor.matches(password, user.getPassword())) {
                        throw new PasswordNotMatedException();
                    }
                    return tokenService.expiring(ImmutableMap.of("uuid",user.getUuid()));
                }).map(this::createTokenMap)
                .orElseThrow(() -> new LoginErrorException(id));
    }

    private Map<String, String> createTokenMap(String token) {
        Map<String, String> map = new HashMap<>();
        map.put("accessToken",token);
        return map;
    }
}
