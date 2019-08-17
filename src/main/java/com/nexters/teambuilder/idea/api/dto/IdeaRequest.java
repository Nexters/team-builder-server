package com.nexters.teambuilder.idea.api.dto;

import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.user.domain.User;
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
    @NotEmpty
    private String title;

    private String content;

    // null 넣었을 때 방지용
    private String file = "";

    private Idea.Type type;

    private boolean selected;
}
