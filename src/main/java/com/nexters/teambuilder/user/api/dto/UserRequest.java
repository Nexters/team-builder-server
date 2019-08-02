package com.nexters.teambuilder.user.api.dto;

import java.time.ZonedDateTime;

import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String id;

    private String password;

    private String name;

    private Integer term;

    private User.Role role;

    private User.Position position;

    private ZonedDateTime createdAt;
}
