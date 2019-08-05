package com.nexters.teambuilder.person.service;

import com.nexters.teambuilder.person.api.dto.PersonRequest;
import com.nexters.teambuilder.person.api.dto.PersonResponse;
import com.nexters.teambuilder.person.domain.Person;
import com.nexters.teambuilder.person.domain.PersonRepository;
import com.nexters.teambuilder.person.exception.PersonNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * person 을 업데이트한다.
     * @param personId id of person
     * @param request for update person
     * @return PersonResponse
     */
    public PersonResponse  updatePerson(Integer personId, PersonRequest request) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));

        person.update(request);

        return PersonResponse.of(personRepository.save(person));
    }

    /**
     * paginated 된 person 목록을 조회한다.
     */
    public Page<PersonResponse> personPage(Pageable pageable) {
        Page<Person> personPage = personRepository.findAll(pageable);

        return personPage.map(PersonResponse::of);
    }
}
