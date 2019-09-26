package com.nexters.teambuilder.user.api.dto;

import java.time.ZonedDateTime;

import javax.validation.constraints.Email;

import com.nexters.teambuilder.user.api.annotation.UniqueId;
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
    @UniqueId
    private String id;

    private String password;

    private String name;

    private Integer nextersNumber;

    private User.Role role;

    private User.Position position;

    @Email
    private String email;

    private ZonedDateTime createdAt;
}
