package com.nexters.teambuilder.user.api;

import com.nexters.teambuilder.user.api.dto.SignInResponse;
import com.nexters.teambuilder.user.api.dto.UserRequest;
import com.nexters.teambuilder.user.api.dto.UserResponse;
import com.nexters.teambuilder.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @PostMapping("sign-up")
    public UserResponse signUp(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("sign-in")
    public SignInResponse signIn(@RequestParam String id, @RequestParam String password) {
        return userService.signIn(id, password);
    }
}
