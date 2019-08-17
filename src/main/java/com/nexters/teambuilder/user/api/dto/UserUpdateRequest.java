package com.nexters.teambuilder.user.api.dto;

import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String nowPassword;

    private String newPassword;

    private User.Position position;
}
