package com.nexters.teambuilder.idea.api.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.nexters.teambuilder.common.view.Views;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.tag.api.dto.TagResponse;
import com.nexters.teambuilder.tag.domain.Tag;
import com.nexters.teambuilder.user.api.dto.UserResponse;
import com.nexters.teambuilder.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@JsonView(Views.List.class)
public class IdeaResponse {
    @JsonView(Views.External.class)
    private Integer ideaId;

    @JsonView(Views.External.class)
    private Integer sessionId;

    @JsonView(Views.External.class)
    private String title;

    @JsonView(Views.External.class)
    private String content;

    @JsonView(Views.External.class)
    private UserResponse author;

    @JsonView(Views.External.class)
    private String file = "";

    @JsonView(Views.External.class)
    private boolean selected;

    @JsonView(Views.External.class)
    private Idea.Type type;

    @JsonView(Views.External.class)
    private Set<TagResponse> tags = new HashSet<>();

    private int orderNumber;

    @JsonView(Views.External.class)
    private int voteNumber;

    @JsonView(Views.External.class)
    private ZonedDateTime createdAt;

    @JsonView(Views.External.class)
    private ZonedDateTime updatedAt;

    public IdeaResponse(Integer ideaId, Integer sessionId,  String title, String content,
                        User author, String file, boolean selected,
                        Idea.Type type,
                        ZonedDateTime createdAt, ZonedDateTime updatedAt,
                        Set<Tag> tags, Integer voteNumber
                        ){
        this.ideaId = ideaId;
        this.sessionId = sessionId;
        this.title = title;
        this.content = content;
        this.author = UserResponse.of(author);
        this.file = file;
        this.selected = selected;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tags = tags.stream()
                .sorted(Comparator.comparing(Tag::getTagId).reversed())
                .map(TagResponse::of).collect(Collectors.toSet());
        this.voteNumber = voteNumber;
    }

    public static IdeaResponse of(Idea idea) {
        return new IdeaResponse(idea.getIdeaId(), idea.getSession().getSessionId(), idea.getTitle(),
                idea.getContent(), idea.getAuthor(), idea.getFile(),
                idea.isSelected(), idea.getType(),
                idea.getCreatedAt(), idea.getUpdateAt(),
                idea.getTags(), idea.getVoteNumber());
    }

    public void updateOrderNumber(Integer orderNumber){
        this.orderNumber = orderNumber;
    }

}
