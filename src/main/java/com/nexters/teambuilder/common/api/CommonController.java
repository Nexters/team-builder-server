package com.nexters.teambuilder.common.api;

import com.nexters.teambuilder.common.Service.CommonService;
import com.nexters.teambuilder.common.api.dto.CommonRequest;
import com.nexters.teambuilder.common.api.dto.CommonResponse;
import com.nexters.teambuilder.common.exception.ActionForbiddenException;
import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/commons")
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;

    @PostMapping
    public BaseResponse<CommonResponse> create(@AuthenticationPrincipal User user, @RequestBody CommonRequest commonRequest) {
        CommonResponse response = commonService.createCommon(commonRequest);
        return new BaseResponse<>(200, 0, response);
    }

    @GetMapping
    public BaseResponse<CommonResponse> get(@AuthenticationPrincipal User user) {
        CommonResponse response = commonService.getCommon();

        return new BaseResponse<>(200, 0, response);
    }

    @PutMapping("{id}")
    public BaseResponse<CommonResponse> update(@AuthenticationPrincipal User user,
                                 @PathVariable Integer id,
                                 @RequestBody CommonRequest request) {
        CommonResponse response = commonService.updateCommon(id, request);
        return new BaseResponse<>(200, 0, response);
    }
}
