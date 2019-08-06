package com.nexters.teambuilder.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.teambuilder.user.api.UserController;
import com.nexters.teambuilder.user.api.dto.SignInResponse;
import com.nexters.teambuilder.user.api.dto.UserRequest;
import com.nexters.teambuilder.user.api.dto.UserResponse;
import com.nexters.teambuilder.user.domain.User;
import com.nexters.teambuilder.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
@WebMvcTest(value = UserController.class, secure = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    private ObjectMapper mapper;

    private FieldDescriptor[] baseResposneDescription = new FieldDescriptor[]{
            fieldWithPath("status").description("status code"),
            fieldWithPath("errorCode").description("error code, 해당 코드를 보고 front 에서 분기처리를 한다"),
            fieldWithPath("data").description("respone data"),
    };

    private FieldDescriptor[] signInResposneDescription = new FieldDescriptor[]{
            fieldWithPath("accessToken").description("access token for user"),
            fieldWithPath("role").description("user role")
    };

    private FieldDescriptor[] userResposneDescription = new FieldDescriptor[]{
            fieldWithPath("uuid").description("user uuid"),
            fieldWithPath("id").description("아이디"),
            fieldWithPath("name").description("user 이름"),
            fieldWithPath("nextersNumber").description("user 기수"),
            fieldWithPath("role").description("user 권한 {ROLE_ADMIN, ROLE_USER}"),
            fieldWithPath("position").description("user Position {DESIGNER, DEVELOPER}"),
            fieldWithPath("createdAt").description("user 가입 일자")
    };

    private FieldDescriptor[] userRequesteDescription = new FieldDescriptor[]{
            fieldWithPath("id").description("아이디"),
            fieldWithPath("password").description("비밀번호"),
            fieldWithPath("name").description("user 이름"),
            fieldWithPath("nextersNumber").description("user 기수"),
            fieldWithPath("role").description("user 권한 {ROLE_ADMIN, ROLE_USER}"),
            fieldWithPath("position").description("user Position {DESIGNER, DEVELOPER}"),
    };

    @BeforeEach
    void setUp() {
        user = new User("originman", "password1212", "kiwon",
                13, User.Role.ROLE_USER, User.Position.DEVELOPER);

        mapper = new ObjectMapper();
    }

    @Test
    void signUp() throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("id", "originman");
        input.put("password", "password1212");
        input.put("name", "kiwon");
        input.put("nextersNumber", 13);
        input.put("role", "ROLE_USER");
        input.put("position", "DEVELOPER");

        given(userService.createUser(any(UserRequest.class))).willReturn(UserResponse.of(user));

        this.mockMvc.perform(post("/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").value("originman"))
                .andExpect(jsonPath("data.name").value("kiwon"))
                .andExpect(jsonPath("data.nextersNumber").value(13))
                .andExpect(jsonPath("data.role").value("ROLE_USER"))
                .andExpect(jsonPath("data.position").value("DEVELOPER"))
                .andDo(document("users/post-signUp",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(userRequesteDescription),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.", userResposneDescription)));
    }

    @Test
    void signIn() throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("id", "originman");
        input.put("password", "password1212");

        given(userService.signIn(anyString(), anyString()))
                .willReturn(new SignInResponse("access token", User.Role.ROLE_USER));

        this.mockMvc.perform(post("/users/sign-in")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("id", "originman'")
                .param("password", "password1212'"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.accessToken").value("access token"))
                .andDo(document("users/post-signIn",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("id").description("user 아이디"),
                                parameterWithName("password").description("user 비밀번호")
                        ),
                        responseFields(baseResposneDescription)
                                        .andWithPrefix("data.", signInResposneDescription)));
    }
}

