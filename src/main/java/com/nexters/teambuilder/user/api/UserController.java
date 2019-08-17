package com.nexters.teambuilder.user.api;

import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.user.api.dto.SignInResponse;
import com.nexters.teambuilder.user.api.dto.UserRequest;
import com.nexters.teambuilder.user.api.dto.UserResponse;
import com.nexters.teambuilder.user.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "회원가")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "originman", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "password", value = "1234", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "name", value = "namkiwon", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "nextersNumber", value = "1", required = true, dataType = "number", paramType = "body"),
            @ApiImplicitParam(name = "role", value = "{ROLE_ADMIN or ROLE_USER}", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "position", value = "{DESIGNER or DEVELOPER}", required = true, dataType = "string", paramType = "body"),
    })
    @PostMapping("sign-up")
    public BaseResponse<UserResponse> signUp(@RequestBody UserRequest request) {
        UserResponse user = userService.createUser(request);

        return new BaseResponse<>(200, 0, user);

    }

    @PostMapping("sign-in")
    public BaseResponse<SignInResponse> signIn(@RequestParam String id, @RequestParam String password) {
        SignInResponse signIn = userService.signIn(id, password);

        return new BaseResponse<>(200, 0, signIn);
    }
}
