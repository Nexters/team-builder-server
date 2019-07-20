package com.nexters.teambuilder.person.api.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.nexters.teambuilder.person.domain.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonRequest {
    @NotNull
    private Person.Gender gender;
    @NotEmpty
    private String name;
    @NotEmpty
    private String nickname;

    private Integer age;
}
