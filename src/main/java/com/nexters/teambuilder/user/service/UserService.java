package com.nexters.teambuilder.user.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nexters.teambuilder.config.security.InValidTokenException;
import com.nexters.teambuilder.config.security.TokenService;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.session.domain.SessionRepository;
import com.nexters.teambuilder.session.exception.SessionNotFoundException;
import com.nexters.teambuilder.user.api.dto.SessionUserResponse;
import com.nexters.teambuilder.user.api.dto.SignInResponse;
import com.nexters.teambuilder.user.api.dto.UserRequest;
import com.nexters.teambuilder.user.api.dto.UserResponse;
import com.nexters.teambuilder.user.api.dto.UserUpdateRequest;
import com.nexters.teambuilder.user.domain.User;
import com.nexters.teambuilder.user.domain.UserRepository;
import com.nexters.teambuilder.user.exception.LoginErrorException;
import com.nexters.teambuilder.user.exception.PasswordNotMatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenService tokenService;

    private final UserRepository userRepository;

    private final SessionRepository sessionRepository;

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

                    Map<String, String> attributes = new LinkedHashMap<>();
                    attributes.put("uuid", user.getUuid());
                    attributes.put("id", user.getId());
                    attributes.put("name", user.getName());
                    attributes.put("nextersNumber", String.valueOf(user.getNextersNumber()));
                    attributes.put("role", String.valueOf(user.getRole()));
                    attributes.put("position", String.valueOf(user.getPosition()));
                    attributes.put("createdAt", String.valueOf(user.getCreatedAt()));

                    return tokenService.expiring(attributes);
                }).map(token -> {
                    User user = findByToken(token);
                    return new SignInResponse(token, user.getRole());
                })
                .orElseThrow(() -> new LoginErrorException(id));
    }

    public void updateUser(User user, UserUpdateRequest request) {
        if (!encryptor.matches(request.getNowPassword(), user.getPassword())) {
            throw new PasswordNotMatedException();
        }

        user.update(encryptor.encode(request.getNewPassword()), request.getPosition());

        userRepository.save(user);
    }

    public List<UserResponse> userList() {
        return userRepository.findAll().stream().map(UserResponse::of).collect(Collectors.toList());
    }

    public List<SessionUserResponse> sessionUserList(Integer sessionNumber) {
        Session session = sessionRepository.findBySessionNumber(sessionNumber)
                .orElseThrow(() -> new SessionNotFoundException(sessionNumber));

        return session.getSessionUsers().stream()
                .map(sessionUser -> new SessionUserResponse(sessionUser))
                .collect(Collectors.toList());
    }
}
