package com.nexters.teambuilder.idea.api.dto;

import com.nexters.teambuilder.idea.domain.Idea;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class VotedIdeaResponse {
    private Integer ideaId;
    private String title;

    public static VotedIdeaResponse of(Idea idea) {
        return new VotedIdeaResponse(idea.getIdeaId(), idea.getTitle());
    }
}
