package com.nexters.teambuilder.tag.api.dto;

import com.nexters.teambuilder.tag.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {
    private Integer tagId;
    private String name;
    private Tag.Type type;

    public static TagResponse of(Tag tag) {
        return new TagResponse(tag.getTagId(), tag.getName(), tag.getType());
    }
}
