package com.nexters.teambuilder.user.api;

import java.util.List;

import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.user.api.dto.UserResponse;
import com.nexters.teambuilder.user.api.dto.UserUpdateRequest;
import com.nexters.teambuilder.user.domain.User;
import com.nexters.teambuilder.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/apis")
public class AuthController {
    private final UserService userService;
    @GetMapping("/me")
    public BaseResponse<UserResponse> me(@AuthenticationPrincipal User user) {

        return new BaseResponse<>(200, 0, UserResponse.of(user));
    }

    @PutMapping("/users")
    public BaseResponse<UserResponse> updateUser(@AuthenticationPrincipal User user,
                                                 @RequestBody UserUpdateRequest request) {
        userService.updateUser(user, request);
        return new BaseResponse<>(200, 0, null);
    }

//    @GetMapping("/users")
//    public BaseResponse<List<UserResponse>> userList() {
//        List<UserResponse> userResponses = userService.userList();
//        return new BaseResponse<>(200, 0, userResponses);
//    }
}
