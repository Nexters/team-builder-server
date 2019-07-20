package com.nexters.teambuilder.person.domain;

import static com.nexters.teambuilder.person.domain.Person.Gender.MAN;
import static org.assertj.core.api.Java6BDDAssertions.then;
import static org.assertj.core.api.Java6BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

class PersonTest {
    @Test
    void constructor_ValidInput_ReturnPerson() {
        Person person = Person.builder()
                .gender(MAN)
                .name("json")
                .nickname("originman")
                .age(27)
                .build();

        then(person)
                .hasFieldOrPropertyWithValue("gender", MAN)
                .hasFieldOrPropertyWithValue("name", "json")
                .hasFieldOrPropertyWithValue("nickname", "originman")
                .hasFieldOrPropertyWithValue("age", 27);
    }

    @Test
    void constructor_NullGender_ThrowIllegalArgumentException() {
        thenThrownBy(() ->Person.builder()
                .gender(null)
                .name("json")
                .nickname("originman")
                .age(27)
                .build()).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_EmptyName_ThrowIllegalArgumentException() {
        thenThrownBy(() ->Person.builder()
                .gender(MAN)
                .name("")
                .nickname("originman")
                .age(27)
                .build()).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_EmptyNickname_ThrowIllegalArgumentException() {
        thenThrownBy(() ->Person.builder()
                .gender(MAN)
                .name("json")
                .nickname("")
                .age(27)
                .build()).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}