package com.nexters.teambuilder.person.service;

import static com.nexters.teambuilder.person.domain.Person.Gender.MAN;
import static org.assertj.core.api.Java6BDDAssertions.then;
import static org.assertj.core.api.Java6BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import com.nexters.teambuilder.person.api.dto.PersonRequest;
import com.nexters.teambuilder.person.api.dto.PersonResponse;
import com.nexters.teambuilder.person.domain.Person;
import com.nexters.teambuilder.person.domain.PersonRepository;
import com.nexters.teambuilder.person.exception.PersonNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    private PersonService personService;
    @Mock
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        this.personService = new PersonService(personRepository);
    }

    @Test
    void create_ValidInput_ValidOutput() {
        //given
        Person person = Person.builder()
                .gender(MAN)
                .name("json")
                .nickname("originman")
                .age(27)
                .build();

        PersonRequest request = new PersonRequest(MAN, "json", "originman", 27);
        given(personRepository.save(any(Person.class))).willReturn(person);
        //when
        PersonResponse result = personService.createPerson(request);

        then(person)
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("gender", MAN)
                .hasFieldOrPropertyWithValue("name", "json")
                .hasFieldOrPropertyWithValue("nickname", "originman")
                .hasFieldOrPropertyWithValue("age", 27)
                .hasFieldOrProperty("bornAt");
    }

    @Test
    void get_ValidId_FoundPerson() {
        //given
        Person person = Person.builder()
                .gender(MAN)
                .name("json")
                .nickname("originman")
                .age(27)
                .build();

        given(personRepository.findById(anyInt())).willReturn(Optional.of(person));

        //when
        PersonResponse result = personService.getPerson(1);

        then(result)
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("gender", MAN)
                .hasFieldOrPropertyWithValue("name", "json")
                .hasFieldOrPropertyWithValue("nickname", "originman")
                .hasFieldOrPropertyWithValue("age", 27)
                .hasFieldOrProperty("bornAt");
    }

    @Test
    void get_InvalidId_ThrowNotFoundException() {
        given(personRepository.findById(anyInt())).willReturn(Optional.empty());

        //then
        thenThrownBy(() -> personService.getPerson(anyInt()))
                .isExactlyInstanceOf(PersonNotFoundException.class);
    }
}