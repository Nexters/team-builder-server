package com.nexters.teambuilder.person.api.dto;

import java.time.ZonedDateTime;

import com.nexters.teambuilder.person.domain.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponse {
    private Integer id;

    private Person.Gender gender;

    private String name;

    private String nickname;

    private Integer age;

    private ZonedDateTime bornAt;

    public static PersonResponse of(Person person) {
        return new PersonResponse(person.getId(), person.getGender(), person.getName(), person.getNickname(),
                person.getAge(), person.getBornAt());
    }
}
