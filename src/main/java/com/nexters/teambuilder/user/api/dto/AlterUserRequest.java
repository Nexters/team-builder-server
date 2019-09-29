package com.nexters.teambuilder.user.api.dto;

import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlterUserRequest {
    private String nowPassword;
    private String newPassword;
    private String confirmNewPassword;
    private User.Position position;
}
