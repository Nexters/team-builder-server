package com.nexters.teambuilder.idea.domain;

import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ideaId;

    private String title;

    private String tags;

    private String position;

    private String author;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @Builder
    public Idea(Integer ideaId, String title, String tags, String position, String author) {
        Assert.notNull(ideaId, "id must not be null");
        Assert.hasLength(title, "title must not be empty");
        Assert.hasLength(position, "position must not be empty");
        Assert.hasLength(author, "author must not be empty");

        this.ideaId = ideaId;
        this.title = title;
        this.tags = tags;
        this.position = position;
        this.author = author;
    }

    public void update(IdeaRequest request) {
        this.ideaId = request.getIdeaId();
        this.title = request.getTitle();
        this.tags = request.getTags();
        this.position = request.getPosition();
        this.author = request.getAuthor();
    }

    public static Idea of(IdeaRequest request) {
        return new Idea(request.getIdeaId(), request.getTitle(), request.getTags(),
                request.getPosition(), request.getAuthor());
    }


}
