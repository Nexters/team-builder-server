package com.nexters.teambuilder.tag.domain;

import javax.persistence.*;

import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.tag.api.dto.TagRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Tag {
    public enum Type {
        DEVELOPER, DESIGNER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tagId;

    private String name;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
                mappedBy = "tags")
    private Set<Idea> ideas = new HashSet<>();

    public Tag(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public void update(TagRequest tagRequest) {
        this.name = tagRequest.getName();
        this.type = tagRequest.getType();
    }

    public static Tag of(TagRequest request) {
        return new Tag(request.getName(), request.getType());
    }

    public void addIdea(Idea idea) {
        this.ideas.add(idea);
    }

    public void removeIdea(Idea idea) {
        this.ideas.remove(idea);
    }
}
