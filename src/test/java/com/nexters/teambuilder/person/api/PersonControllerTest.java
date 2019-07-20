package com.nexters.teambuilder.person.api;

import static com.nexters.teambuilder.person.domain.Person.Gender.MAN;
import static java.time.ZonedDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private FieldDescriptor[] pageDescriptor = new FieldDescriptor[]{
            fieldWithPath("pageable")
                    .description("The Pageable that's been used to request the current Slice."),
            fieldWithPath("totalElements").description("The number of total numbers"),
            fieldWithPath("totalPages").description("Total count of pages"),
            fieldWithPath("size").description("The size of current page"),
            fieldWithPath("number").description("The number of the current page"),
            fieldWithPath("numberOfElements")
                    .description("The number of Numbers currently on this page."),
            fieldWithPath("sort").description("The sorting parameters for the page."),
            fieldWithPath("sort.sorted").description("Whether the current page is sorted"),
            fieldWithPath("sort.unsorted").description("Whether the current page is unsorted"),
            fieldWithPath("sort.empty").description("Whether sort order is empty"),
            fieldWithPath("first").description("Whether the current page is the first one"),
            fieldWithPath("last").description("Whether the current page is the last one"),
            fieldWithPath("empty").description("Whether the contents are empty"),
            fieldWithPath("content[]").description("An array of persons"),
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
    @DisplayName("GET /persons/{id} then return found Person")
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

    @Test
    @DisplayName("PUT /persons/{id} then return updated Person")
    void put_ValidInput_ReturnUpdatedPerson() throws Exception {
        Person person = new Person(1, MAN, "json", "originman", 27, ZonedDateTime.now());

        given(personService.updatePerson(anyInt(), any(PersonRequest.class))).willReturn(PersonResponse.of(person));

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("gender", 1);
        input.put("name", "json");
        input.put("nickname", "originman");
        input.put("age", 27);

        String inputJson = mapper.writeValueAsString(input);
        // expect
        this.mockMvc.perform(put("/persons/{personId}", 1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value("json"))
                .andExpect(jsonPath("nickname").value("originman"))
                .andExpect(jsonPath("age").value(27))
                .andExpect(jsonPath("bornAt").exists())
                .andDo(document("persons/put-person",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("personId").description("Person의 id")
                                        .attributes(key("constraints").value("Not Null"))),
                        requestFields(personRequestDescription),
                        responseFields(personResposneDescription)));
    }

    @Test
    void page_WithPageable_ReturnPaginatedPersonList() throws Exception {
        List<PersonResponse> persons = IntStream.range(0, 10)
                .mapToObj(i -> new Person(10 - i, MAN, "json", "originman", 27, now()))
                .map(PersonResponse::of)
                .collect(Collectors.toList());

        given(personService.personPage(any(Pageable.class))).willReturn(new PageImpl<>(persons));
        // expect
        this.mockMvc.perform(get("/persons?page=1&size=10&sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(10))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andDo(document("persons/list-person",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("page").description("The page to retrieve").optional()
                                        .attributes(key("constraints").value("기본값: 1")),
                                parameterWithName("size").description("Entries size per page").optional()
                                        .attributes(key("constraints").value("기본값: 10")),
                                parameterWithName("sort")
                                        .description("Sorting option. format -> '{columnName},(asc|desc)'").optional()
                                        .attributes(key("constraints").value("기본값: id,desc"))),
                        responseFields(pageDescriptor)
                                .andWithPrefix("content[].", personResposneDescription)));
    }
}