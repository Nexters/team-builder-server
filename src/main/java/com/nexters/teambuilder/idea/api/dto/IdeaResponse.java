package com.nexters.teambuilder.idea.api.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.nexters.teambuilder.common.view.Views;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.tag.domain.Tag;
import com.nexters.teambuilder.user.api.dto.UserResponse;
import com.nexters.teambuilder.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
@JsonView(Views.List.class)
public class IdeaResponse {
    @JsonView(Views.External.class)
    private Integer ideaId;

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

    private Set<Tag> tags = new HashSet<Tag>();

    private int orderNumber;

    @JsonView(Views.External.class)
    private ZonedDateTime createdAt;

    @JsonView(Views.External.class)
    private ZonedDateTime updatedAt;

    public IdeaResponse(Integer ideaId, String title, String content,
                        User author, String file, boolean selected,
                        Idea.Type type,
                        ZonedDateTime createdAt, ZonedDateTime updatedAt,
                        Tag... tags){
        this.ideaId = ideaId;
        this.title = title;
        this.content = content;
        this.author = UserResponse.of(author);
        this.file = file;
        this.selected = selected;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        /* this에서 에러남
        this.tags = Stream.of(tags).collect(Collectors.toSet());
        this.tags.forEach(x->x.getIdeas().add(this));
         */
    }

    public static IdeaResponse of(Idea idea) {
        return new IdeaResponse(idea.getIdeaId(), idea.getTitle(),
                idea.getContent(), idea.getAuthor(), idea.getFile(),
                idea.isSelected(), idea.getType(),
                idea.getCreatedAt(), idea.getUpdateAt()
                //idea.getTags() 이것도 안됨
                );
    }

    public void updateOrderNumber(Integer orderNumber){
        this.orderNumber = orderNumber;
    }

}
