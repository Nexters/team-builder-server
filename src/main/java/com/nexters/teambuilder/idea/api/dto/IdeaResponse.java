package com.nexters.teambuilder.idea.api.dto;

import com.nexters.teambuilder.idea.domain.Idea;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IdeaResponse {

    private Integer ideaId;

    private String title;

    private String tags;

    private String position;

    private String author;

    private ZonedDateTime createdAt;

    public static IdeaResponse of(Idea idea) {
        return new IdeaResponse(idea.getIdeaId(), idea.getTitle(), idea.getTags(),
                idea.getPosition(), idea.getAuthor(), idea.getCreatedAt());
    }

}
