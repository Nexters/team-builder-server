package com.nexters.teambuilder.common.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommonResponse {
    public Integer id;

    public Integer authenticationCode;
}
