package com.nexters.teambuilder.person.domain;

import static com.nexters.teambuilder.person.domain.Person.Gender.MAN;
import static org.assertj.core.api.Java6BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PersonRepositoryTest {
    @Autowired
    private PersonRepository personRepository;

    @Test
    void save_WithValidInput_ReturnSavedPerson() {
        Person person = Person.builder()
                .gender(MAN)
                .name("json")
                .nickname("originman")
                .age(27)
                .build();

        Person result = personRepository.save(person);

        then(result)
                .hasFieldOrPropertyWithValue("gender", MAN)
                .hasFieldOrPropertyWithValue("name", "json")
                .hasFieldOrPropertyWithValue("nickname", "originman")
                .hasFieldOrPropertyWithValue("age", 27);
    }
}