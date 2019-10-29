package com.nexters.teambuilder.session.api.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;

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
    @NotEmpty
    @URL
    private String logoImageUrl;

    private boolean teamBuildingMode;

    private List<PeriodRequest> periods;

    private int maxVoteCount;
}
