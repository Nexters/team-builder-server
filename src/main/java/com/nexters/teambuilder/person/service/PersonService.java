package com.nexters.teambuilder.person.service;

import com.nexters.teambuilder.person.api.dto.PersonRequest;
import com.nexters.teambuilder.person.api.dto.PersonResponse;
import com.nexters.teambuilder.person.domain.Person;
import com.nexters.teambuilder.person.domain.PersonRepository;
import com.nexters.teambuilder.person.exception.PersonNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PersonService {
    private final PersonRepository personRepository;

    /**
     * person 을 등록한다.
     * @param request for create person
     * @return PersonResponse
     */
    public PersonResponse createPerson(PersonRequest request) {
        return PersonResponse.of(personRepository.save(Person.of(request)));
    }

    public PersonResponse getPerson(Integer personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));

        return PersonResponse.of(person);
    }
}
