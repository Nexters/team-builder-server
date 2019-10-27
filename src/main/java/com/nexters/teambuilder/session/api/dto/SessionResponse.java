package com.nexters.teambuilder.session.api.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.api.dto.VotedIdeaResponse;
import com.nexters.teambuilder.session.domain.Period;
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

    private List<SessionNumber> sessionNumbers;

    private String logoImageUrl;

    private boolean teamBuildingMode;

    private List<PeriodResponse> periods;

    private List<TagResponse> tags;

    private Integer maxVoteCount;

    private List<IdeaResponse> ideas = new ArrayList<>();

    private List<VotedIdeaResponse> votedIdeas = new ArrayList<>();

    public static SessionResponse of(Session session, List<SessionNumber> sessionNumbers,  List<TagResponse> tags,
                                     List<IdeaResponse> ideas, List<VotedIdeaResponse> votedIdeas) {
        List<PeriodResponse> periods =
                session.getPeriods().stream().map(PeriodResponse::of).collect(Collectors.toList());

        if (session.isTeamBuildingMode()) {
            periods.forEach(periodResponse -> periodResponse.setNow(false));
            periods.stream()
                    .filter(periodResponse -> periodResponse.getPeriodType().equals(Period.PeriodType.TEAM_BUILDING))
                    .findFirst().ifPresent(periodResponse -> periodResponse.setNow(true));
        }

        nowIsNotMatchAnyPeriod(periods);

        return new SessionResponse(session.getSessionId(), session.getSessionNumber(), sessionNumbers, session.getLogoImageUrl(),
                session.isTeamBuildingMode(), periods, tags, session.getMaxVoteCount(), ideas, votedIdeas);
    }

    private static void nowIsNotMatchAnyPeriod(List<PeriodResponse> periods) {
        if (!periods.stream().anyMatch(period -> period.isNow())) {

            periods.stream().sorted(Comparator.comparing(PeriodResponse::getStartDate)).findFirst().ifPresent(p -> {
                p.setNow(true);
            });

            ZonedDateTime now = ZonedDateTime.now();
            periods.stream().sorted(Comparator.comparing(PeriodResponse::getStartDate)).forEach(periodResponse -> {
                if (now.isAfter(periodResponse.getEndDate())) {
                    periods.forEach(p -> p.setNow(false));
                    periodResponse.setNow(true);
                }
            });
        }
    }

}
