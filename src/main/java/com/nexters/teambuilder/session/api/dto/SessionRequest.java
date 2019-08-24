package com.nexters.teambuilder.session.api.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.nexters.teambuilder.user.domain.User;
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

    private boolean teamBuildingMode;

    private List<PeriodRequest> periods;

    private int maxVoteCount;
}
