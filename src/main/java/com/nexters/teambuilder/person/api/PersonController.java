package com.nexters.teambuilder.person.api;

import javax.validation.Valid;

import com.nexters.teambuilder.person.api.dto.PersonRequest;
import com.nexters.teambuilder.person.api.dto.PersonResponse;
import com.nexters.teambuilder.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("persons")
public class PersonController {
    private final PersonService personService;

    @PostMapping
    public PersonResponse crerate(@RequestBody @Valid PersonRequest request) {
        return personService.createPerson(request);
    }

    /**
     * GET /persons/{personId}
     * @param personId
     * @return
     */
    @GetMapping("{personId}")
    public PersonResponse crerate(@PathVariable Integer personId) {
        return personService.getPerson(personId);
    }
}
