package com.nexters.teambuilder.idea.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IdeaRequest {
    @NotNull
    private Integer ideaId;

    @NotEmpty
    private String title;

    private String tags;

    @NotEmpty
    private String position;

    @NotEmpty
    private String author;

}
