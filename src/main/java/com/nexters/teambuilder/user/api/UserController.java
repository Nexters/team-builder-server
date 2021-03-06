package com.nexters.teambuilder.user.api;

import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.user.api.dto.*;
import com.nexters.teambuilder.user.domain.User;
import com.nexters.teambuilder.user.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "회원가입")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "originman", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "password", value = "1234", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "name", value = "namkiwon", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "nextersNumber", value = "1", required = true, dataType = "number", paramType = "body"),
            @ApiImplicitParam(name = "role", value = "{ROLE_ADMIN or ROLE_USER}", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "position", value = "{DESIGNER or DEVELOPER}", required = true, dataType = "string", paramType = "body"),
    })

    @GetMapping("/users/check-id")
    public BaseResponse<Map> checkIdDuplicate(String id) {
        boolean isIdUsable = userService.isIdUsable(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isIdUsable", isIdUsable);

        return new BaseResponse<>(200, 0, response);
    }

    @PostMapping("/users/sign-up")
    public BaseResponse<UserResponse> signUp(@RequestBody @Valid UserRequest request) {
        UserResponse user = userService.createUser(request);

        return new BaseResponse<>(200, 0, user);

    }

    @PostMapping("/users/sign-in")
    public BaseResponse<SignInResponse> signIn(@RequestParam String id, @RequestParam String password) {
        SignInResponse signIn = userService.signIn(id, password);

        return new BaseResponse<>(200, 0, signIn);
    }

    @GetMapping("apis/users")
    public BaseResponse<List<UserResponse>> userList() {
        List<UserResponse> userResponses = userService.userList();
        return new BaseResponse<>(200, 0, userResponses);
    }

    @GetMapping("apis/activated/users")
    public BaseResponse<List<UserResponse>> activatedUserList() {
        List<UserResponse> userResponses = userService.activatedUserList();
        return new BaseResponse<>(200, 0, userResponses);
    }

    @GetMapping("apis/sessions/{sessionNumber}/users")
    public BaseResponse<List<SessionUserResponse>> userList(@PathVariable Integer sessionNumber) {
        List<SessionUserResponse> sessionUserResponses = userService.sessionUserList(sessionNumber);
        return new BaseResponse<>(200, 0, sessionUserResponses);
    }

    @GetMapping("apis/sessions/{sessionNumber}/users/{uuid}")
    public BaseResponse<SessionUserResponse> sessionUser(@PathVariable Integer sessionNumber,
                                                         @PathVariable String uuid) {
        SessionUserResponse sessionUserResponses = userService.getSessionUser(sessionNumber, uuid);
        return new BaseResponse<>(200, 0, sessionUserResponses);
    }

    @PutMapping("apis/users/{uuid}/activate")
    public BaseResponse<List<UserResponse>> activateUser(@PathVariable String uuid) {
        System.out.println(uuid);
        userService.activateUser(uuid);

        return new BaseResponse<>(200, 0, null);
    }

    @PutMapping("apis/users/{uuid}/deactivate")
    public BaseResponse<List<UserResponse>> deactivateUser(@PathVariable String uuid) {
        userService.deactivateUser(uuid);

        return new BaseResponse<>(200, 0, null);
    }

    @PutMapping("apis/users/deactivate/all")
    public BaseResponse<List<UserResponse>> deactivateAllUsers() {
        List<UserResponse> users = userService.deactivateAllUsers();

        return new BaseResponse<>(200, 0, users);
    }

    @PutMapping("apis/users")
    public BaseResponse<UserResponse> updateUser(@AuthenticationPrincipal User user,
                                                 @RequestBody UserUpdateRequest request) {
        userService.updateUser(user, request);
        return new BaseResponse<>(200, 0, null);
    }

    @PutMapping("apis/users/dismiss")
    public BaseResponse dismiss(@AuthenticationPrincipal User user,
                                @RequestBody UserDismissRequest request) {
        userService.dismissUsers(user, request);

        return new BaseResponse<>(200, 0, null);
    }
}