package com.nexters.teambuilder.tag.api.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.nexters.teambuilder.tag.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagRequest {
    @NotEmpty
    private String name;
    @NotNull
    private Tag.Type type;
}
