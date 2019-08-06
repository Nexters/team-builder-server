package com.nexters.teambuilder.user.api.dto;

import java.time.ZonedDateTime;

import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String uuid;

    private String id;

    private String name;

    private Integer nextersNumber;

    private User.Role role;

    private User.Position position;

    private ZonedDateTime createdAt;

    public static UserResponse of(User user) {
        return new UserResponse(user.getUuid(), user.getId(), user.getName(),
                user.getNextersNumber(), user.getRole(), user.getPosition(), user.getCreatedAt());
    }
}
