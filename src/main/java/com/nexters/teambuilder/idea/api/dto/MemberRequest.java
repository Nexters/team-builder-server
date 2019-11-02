package com.nexters.teambuilder.idea.api.dto;

import com.nexters.teambuilder.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest {
    List<String> uuids;
}
