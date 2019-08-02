package com.nexters.teambuilder.user.api;

import java.util.Map;

import com.nexters.teambuilder.user.api.dto.UserRequest;
import com.nexters.teambuilder.user.api.dto.UserResponse;
import com.nexters.teambuilder.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @PostMapping("sign-up")
    public UserResponse signUp(@RequestBody UserRequest request) {
        return UserResponse.of(userService.createUser(request));
    }

    @PostMapping("sign-in")
    public Map<String, String> signIn(@RequestParam String id, @RequestParam String password) {
        return userService.logIn(id, password);
    }
}
