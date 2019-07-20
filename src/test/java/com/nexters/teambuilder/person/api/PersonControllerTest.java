package com.nexters.teambuilder.person.api;

import static com.nexters.teambuilder.person.domain.Person.Gender.MAN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.teambuilder.person.api.dto.PersonRequest;
import com.nexters.teambuilder.person.api.dto.PersonResponse;
import com.nexters.teambuilder.person.domain.Person;
import com.nexters.teambuilder.person.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "${service.api-server}", uriPort = 80)
@WebMvcTest(value = PersonController.class, secure = false)
class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    private FieldDescriptor[] personRequestDescription = new FieldDescriptor[]{
            fieldWithPath("gender").description("person's gender"),
            fieldWithPath("name").description("person's name"),
            fieldWithPath("nickname").description("person's nickname"),
            fieldWithPath("age").description("person's age")
    };

    private FieldDescriptor[] personResposneDescription = new FieldDescriptor[]{
            fieldWithPath("id").description("person id"),
            fieldWithPath("gender").description("person's gender"),
            fieldWithPath("name").description("person's name"),
            fieldWithPath("nickname").description("person's nickname"),
            fieldWithPath("age").description("person's age"),
            fieldWithPath("bornAt").description("when person is born at"),
    };

    @Test
    @DisplayName("POST /persons then return created Person")
    void create_ValidInput_ReturnCreatedPerson() throws Exception {
        Person person = new Person(1, MAN, "json", "originman", 27, ZonedDateTime.now());

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("gender", 1);
        input.put("name", "json");
        input.put("nickname", "originman");
        input.put("age", 27);

        String inputJson = mapper.writeValueAsString(input);

        given(personService.createPerson(any(PersonRequest.class))).willReturn(PersonResponse.of(person));

        // expect
        this.mockMvc.perform(post("/persons")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value("json"))
                .andExpect(jsonPath("nickname").value("originman"))
                .andExpect(jsonPath("age").value(27))
                .andExpect(jsonPath("bornAt").exists())
                .andDo(document("persons/post-person",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(personRequestDescription),
                        responseFields(personResposneDescription)));
    }

    @Test
    @DisplayName("GET /persons/{id} then return created Person")
    void get_ValidInput_ReturnFoundPerson() throws Exception {
        Person person = new Person(1, MAN, "json", "originman", 27, ZonedDateTime.now());

        given(personService.getPerson(anyInt())).willReturn(PersonResponse.of(person));

        // expect
        this.mockMvc.perform(get("/persons/{personId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value("json"))
                .andExpect(jsonPath("nickname").value("originman"))
                .andExpect(jsonPath("age").value(27))
                .andExpect(jsonPath("bornAt").exists())
                .andDo(document("persons/get-person",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("personId").description("Person의 id")
                                        .attributes(key("constraints").value("Not Null"))),
                        responseFields(personResposneDescription)));
    }
}