package com.nexters.teambuilder.common.Service;

import com.nexters.teambuilder.common.api.dto.CommonRequest;
import com.nexters.teambuilder.common.api.dto.CommonResponse;
import com.nexters.teambuilder.common.domain.Common;
import com.nexters.teambuilder.common.domain.CommonRepository;
import com.nexters.teambuilder.common.exception.CommonNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final CommonRepository commonRepository;

    public CommonResponse createCommon(CommonRequest commonRequest) {
        Common common =  commonRepository.save(Common.of(commonRequest));
        return new CommonResponse(common.getId(), common.getAuthenticationCode());
    }

    public CommonResponse getCommon() {
        Common common =  commonRepository.findTopByOrderByIdDesc().orElseThrow(() -> new CommonNotFoundException(0));

        return new CommonResponse(common.getId(), common.getAuthenticationCode());
    }

    public CommonResponse updateCommon(Integer id, CommonRequest commonRequest) {
        Common common =  commonRepository.findById(id).orElseThrow(() -> new CommonNotFoundException(id));

        common.update(commonRequest.getAuthenticationCode());

        Common updatedCommon = commonRepository.save(common);

        return new CommonResponse(updatedCommon.getId(), updatedCommon.getAuthenticationCode());
    }
}
