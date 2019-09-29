package com.nexters.teambuilder.common.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.nexters.teambuilder.common.api.dto.CommonRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Common {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer authenticationCode;

    public Common(Integer authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public static Common of(CommonRequest request) {
        return new Common(request.getAuthenticationCode());
    }

    public void update(Integer authenticationCode) {
        this.authenticationCode = authenticationCode;
    }
}
