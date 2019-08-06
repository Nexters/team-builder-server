package com.nexters.teambuilder.tag.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.nexters.teambuilder.tag.api.dto.TagRequest;
import lombok.Getter;

@Entity
@Getter
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
}
