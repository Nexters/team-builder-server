package com.nexters.teambuilder.person.service;

import static com.nexters.teambuilder.person.domain.Person.Gender.MAN;
import static java.time.ZonedDateTime.now;
import static org.assertj.core.api.Java6BDDAssertions.then;
import static org.assertj.core.api.Java6BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.nexters.teambuilder.person.api.dto.PersonRequest;
import com.nexters.teambuilder.person.api.dto.PersonResponse;
import com.nexters.teambuilder.person.domain.Person;
import com.nexters.teambuilder.person.domain.PersonRepository;
import com.nexters.teambuilder.person.exception.PersonNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    private PersonService personService;
    @Mock
    private PersonRepository personRepository;
    @Captor
    private ArgumentCaptor<Person> personCaptor;

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
        personService.createPerson(request);
        verify(personRepository).save(personCaptor.capture());

        then(personCaptor.getValue())
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

    @Test
    void update_ValidInput_ReturnUpdatedPerson() {
        //given
        Person person = new Person(1, MAN, "json", "originman", 27, now());

        given(personRepository.findById(anyInt())).willReturn(Optional.of(person));

        PersonRequest request = new PersonRequest(MAN, "json", "originman", 27);
        given(personRepository.save(any(Person.class))).willReturn(person);

        //when
        personService.updatePerson(1, request);
        verify(personRepository).save(personCaptor.capture());

        then(personCaptor.getValue())
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("gender", MAN)
                .hasFieldOrPropertyWithValue("name", "json")
                .hasFieldOrPropertyWithValue("nickname", "originman")
                .hasFieldOrPropertyWithValue("age", 27)
                .hasFieldOrProperty("bornAt");

    }

    @Test
    void page_WithPageable_ReturnPaginatedPersonList() {
        //given
        List<Person> persons = IntStream.range(0, 10)
                .mapToObj(i -> new Person(10 - i, MAN, "json", "originman", 27, now()))
                .collect(Collectors.toList());

        given(personRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(persons));

        //when
        Page<PersonResponse> personPage = personService.personPage(PageRequest.of(1, 10));
        //then
        then(personPage.getTotalElements())
                .isEqualTo(10);
    }
}