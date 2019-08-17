package com.nexters.teambuilder.session.api.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionRequest {

    @NotNull
    private Integer sessionNumber;

    @NotEmpty
    @URL
    private String logoImageUrl;

    private List<PeriodRequest> periods;
}
