package com.nexters.teambuilder.person.api;

import static org.springframework.data.domain.Sort.Direction.DESC;

import javax.validation.Valid;

import com.nexters.teambuilder.person.api.dto.PersonRequest;
import com.nexters.teambuilder.person.api.dto.PersonResponse;
import com.nexters.teambuilder.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("persons")
public class PersonController {
    private final PersonService personService;

    @PostMapping
    public PersonResponse create(@RequestBody @Valid PersonRequest request) {
        return personService.createPerson(request);
    }

    /**
     * GET /persons/{personId}.
     * @param personId id of person
     * @return PersonResponse
     */
    @GetMapping("{personId}")
    public PersonResponse get(@PathVariable Integer personId) {
        return personService.getPerson(personId);
    }

    /**
     * GET /persons.
     * @param pageable for pagination
     * @return Page<PersonResponse>
     */
    @GetMapping
    public Page<PersonResponse> page(@PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        return personService.personPage(pageable);
    }

    /**
     * PUT /persons/{personId}.
     * @param personId id of person
     * @param request for update
     * @return PersonResponse
     */
    @PutMapping("{personId}")
    public PersonResponse update(@PathVariable Integer personId, @RequestBody @Valid PersonRequest request) {
        return personService.updatePerson(personId, request);
    }
}
