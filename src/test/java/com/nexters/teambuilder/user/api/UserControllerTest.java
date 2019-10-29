package com.nexters.teambuilder.user.api;

import static com.nexters.teambuilder.user.domain.User.Position.DEVELOPER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
            fieldWithPath("email").description("user email"),
            fieldWithPath("activated").description("user 활성화 여부"),
            fieldWithPath("position").description("user Position {DESIGNER, DEVELOPER}"),
            fieldWithPath("role").description("user 권한 {ROLE_ADMIN, ROLE_USER}"),
            fieldWithPath("voteCount").description("user 아이디어 투표 횟수"),
            fieldWithPath("voted").description("user 투표 여부"),
            fieldWithPath("submitIdea").description("user 아이디어 제출 여부"),
            fieldWithPath("hasTeam").description("user 팀 소속 여부"),
            fieldWithPath("createdAt").description("user 가입 일자"),
    };

    private FieldDescriptor[] userRequesteDescription = new FieldDescriptor[]{
            fieldWithPath("id").description("아이디"),
            fieldWithPath("password").description("비밀번호"),
            fieldWithPath("name").description("user 이름"),
            fieldWithPath("nextersNumber").description("user 기수"),
            fieldWithPath("email").description("user email"),
            fieldWithPath("role").description("user 권한 {ROLE_ADMIN, ROLE_USER}"),
            fieldWithPath("position").description("user Position {DESIGNER, DEVELOPER}"),
            fieldWithPath("authenticationCode").description("회원가입을 위한 인증코드"),
    };

    private FieldDescriptor[] userUpdateRequesteDescription = new FieldDescriptor[]{
            fieldWithPath("nowPassword").description("현재 비밀번호"),
            fieldWithPath("newPassword").description("변경할 비밀번호 (null 일시 비밀번호 변경 스킵)"),
            fieldWithPath("position").description("user Position {DESIGNER, DEVELOPER} (null 일시 변경 스킵)"),
    };

    @BeforeEach
    void setUp() {
        user = new User("originman", "password1212", "kiwon",
                13, User.Role.ROLE_USER, User.Position.DEVELOPER, "originman@nexter.com");

        mapper = new ObjectMapper();
    }

    @Test
    void checkId() throws Exception {
        given(userService.isIdUsable(anyString())).willReturn(true);

        this.mockMvc.perform(get("/users/check-id").param("id", "test1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.isIdUsable").value(true))
                .andDo(document("users/get-checkId",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("id").description("중복여부를 판단할 ID")
                        ),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.",
                                        fieldWithPath("isIdUsable").description("아이디 사용 가능여부"))));
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
        input.put("email", "originman@nexter.com");
        input.put("authenticationCode", 12345);

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

    @Test
    void list() throws Exception {
        List<UserResponse> users = IntStream.range(1, 11).mapToObj(i -> {
            user = new User("originman" + i, "password1212", "kiwon",
                    13, User.Role.ROLE_USER, User.Position.DEVELOPER, "originman@nexter.com");
            user.activate();

            return UserResponse.of(user);
        }).collect(Collectors.toList());

        given(userService.userList()).willReturn(users);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/apis/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("users/list-users",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.[].", userResposneDescription)));
    }

    @Test
    void activatedUserList() throws Exception {
        List<UserResponse> users = IntStream.range(1, 4).mapToObj(i -> {
            user = new User("originman" + i, "password1212", "kiwon",
                    13, User.Role.ROLE_USER, User.Position.DEVELOPER, "originman@nexter.com");
            user.activate();

            return UserResponse.of(user);
        }).collect(Collectors.toList());

        given(userService.activatedUserList()).willReturn(users);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/apis/activated/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("users/list-activated-users",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.[].", userResposneDescription)));
    }

    @Test
    void updateUser() throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("nowPassword", "1212");
        input.put("newPassword", "3434");
        input.put("position", DEVELOPER);

        this.mockMvc.perform(put("/apis/users")
                .header("Authorization", "Bearer " + "<access_token>")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andDo(document("users/put-user",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(userUpdateRequesteDescription),
                        responseFields(baseResposneDescription)));
    }

    @Test
    void activate() throws Exception {
        List<UserResponse> users = IntStream.range(1, 11).mapToObj(i -> {
            user = new User("originman" + i, "password1212", "kiwon",
                    13, User.Role.ROLE_USER, User.Position.DEVELOPER, "originman@nexter.com");
            user.activate();

            return UserResponse.of(user);
        }).collect(Collectors.toList());

        this.mockMvc.perform(put("/apis/users/{uuid}/activate", "aaaa-aaa-aaaa-aaaa")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("users/put-activate",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("uuid").description("활성화시킬 user uuid")
                        ),
                        responseFields(baseResposneDescription)));
    }

    @Test
    void deactivate() throws Exception {
        List<UserResponse> users = IntStream.range(1, 11).mapToObj(i -> {
            user = new User("originman" + i, "password1212", "kiwon",
                    13, User.Role.ROLE_USER, User.Position.DEVELOPER, "originman@nexter.com");
            user.activate();

            return UserResponse.of(user);
        }).collect(Collectors.toList());

        this.mockMvc.perform(put("/apis/users/{uuid}/deactivate", "aaaa-aaa-aaaa-aaaa")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("users/put-deactivate",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("uuid").description("비활성화시킬 user uuid")
                        ),
                        responseFields(baseResposneDescription)));
    }

    @Test
    void deactivateAll() throws Exception {
        List<UserResponse> users = IntStream.range(1, 11).mapToObj(i -> {
            user = new User("originman" + i, "password1212", "kiwon",
                    13, User.Role.ROLE_USER, User.Position.DEVELOPER, "originman@nexter.com");
            user.deactivate();

            return UserResponse.of(user);
        }).collect(Collectors.toList());

        given(userService.deactivateAllUsers()).willReturn(users);

        this.mockMvc.perform(put("/apis/users/deactivate/all")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("users/put-deactivate-all",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.[].", userResposneDescription)));
    }
}

