package com.nexters.teambuilder.person.service;

import com.nexters.teambuilder.person.api.dto.PersonRequest;
import com.nexters.teambuilder.person.api.dto.PersonResponse;
import com.nexters.teambuilder.person.domain.Person;
import com.nexters.teambuilder.person.domain.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonResponse createPerson(PersonRequest request) {
        return PersonResponse.of(personRepository.save(Person.of(request)));
    }
}
