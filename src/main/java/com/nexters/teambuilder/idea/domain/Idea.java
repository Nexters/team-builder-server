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
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Idea {

    public enum Type{
        IDEA, NOTICE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ideaId;

    /*
    @NotNull
    @Size(max = 100)
    */
    private String title;

    @Column(columnDefinition = "TEXT", name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    private String file;

    private boolean selected;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "idea_tag",
            joinColumns = { @JoinColumn(name = "idea_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private ZonedDateTime updateAt;

    @Builder
    public Idea(String title, String content, User author, String file, Type type, List<Tag> tags) {
        Assert.hasLength(title, "title must not be empty");
        Assert.notNull(author, "author must not be null");

        this.title = title;
        this.content = content;
        this.author = author;
        this.file = file;
        this.type = type;
        this.tags = tags.stream().collect(Collectors.toSet());
    }

    public void update(IdeaRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.file = request.getFile();
        this.type = request.getType();
        this.selected = request.isSelected();
    }

    public static Idea of(User author, List<Tag> tags, IdeaRequest request) {
        return new Idea(request.getTitle(), request.getContent(),
                author, request.getFile(), request.getType(), tags);
    }


}
