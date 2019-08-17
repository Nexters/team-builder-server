package com.nexters.teambuilder.session.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.tag.api.dto.TagResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {
    private Integer sessionId;

    private Integer sessionNumber;

    private String logoImageUrl;

    private List<PeriodResponse> periods;

    private List<TagResponse> tags;

    private List<IdeaResponse> ideas = new ArrayList<>();

    public static SessionResponse of(Session session, List<TagResponse> tags, List<IdeaResponse> ideas) {
        List<PeriodResponse> periods =
                session.getPeriods().stream().map(PeriodResponse::of).collect(Collectors.toList());

        return new SessionResponse(session.getSessionId(), session.getSessionNumber(), session.getLogoImageUrl(),
                periods,tags, ideas);
    }
}
