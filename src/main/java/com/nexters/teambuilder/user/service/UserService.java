package com.nexters.teambuilder.user.service;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.nexters.teambuilder.config.security.InValidTokenException;
import com.nexters.teambuilder.config.security.TokenService;
import com.nexters.teambuilder.user.api.dto.SignInResponse;
import com.nexters.teambuilder.user.api.dto.UserRequest;
import com.nexters.teambuilder.user.api.dto.UserResponse;
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

    public UserResponse createUser(UserRequest request) {

        User user = userRepository.save(User.builder()
                .id(request.getId())
                .password(encryptor.encode(request.getPassword()))
                .name(request.getName())
                .nextersNumber(request.getNextersNumber())
                .role(request.getRole())
                .position(request.getPosition())
                .build());

        return UserResponse.of(user);
    }

    public User findByToken(String token) {
        return Optional.of(tokenService.verify(token))
                .map(map -> map.get("uuid"))
                .flatMap(userRepository::findUserByUuid)
                .orElseThrow(() -> new InValidTokenException());
    }

    public SignInResponse signIn(String id, String password) {
        return userRepository.findUserById(id)
                .map(user -> {
                    user.setAuthenticated(true);
                    if(!encryptor.matches(password, user.getPassword())) {
                        throw new PasswordNotMatedException();
                    }
                    return tokenService.expiring(user,
                            ImmutableMap.of("uuid",user.getUuid(),
                                    "id", user.getId(),
                                    "name", user.getName(),
                                    "nextersNumber", user.getNextersNumber(),
                                    "role", user.getRole(),
                                    ""));
                }).map(token -> {
                    User user = findByToken(token);
                    return new SignInResponse(token, user.getRole());
                })
                .orElseThrow(() -> new LoginErrorException(id));
    }
}
