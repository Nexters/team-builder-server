package com.nexters.teambuilder.idea.domain;

import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.tag.domain.Tag;
import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.io.File;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "idea")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Idea {

    public enum Type{
        IDEA, NOTICE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ideaId; // null 때문에!!

    private String title;

    @Column(columnDefinition = "TEXT", name = "content", nullable = false)
    private String content;

//    @ManyToMany // 중요...! 뭔가 중간 객체..?
//    @JoinColumn(name = "idea_id")
//    private List<Tag> tags;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;


    private String file;

    //    // 이것도 디비에..?
    private boolean selected;

    @Enumerated(EnumType.STRING)
    private Type type;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private ZonedDateTime updateAt;

    @Builder
    public Idea(Integer ideaId, String title, String content, User author, String file, Type type) {
        Assert.notNull(ideaId, "id must not be null");
        Assert.hasLength(title, "title must not be empty");
        Assert.notNull(author, "author must not be null");

        this.ideaId = ideaId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.file = file;
        this.type = type;
    }

    public void update(IdeaRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.file = request.getFile();
        this.type = request.getType();
        this.selected = request.isSelected();
    }

    public static Idea of(User author, IdeaRequest request) {
        return new Idea(request.getIdeaId(), request.getTitle(), request.getContent(),
                author, request.getFile(), request.getType());
    }


}
