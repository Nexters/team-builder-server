package com.nexters.teambuilder.user.api;

import static com.nexters.teambuilder.user.domain.User.Position.DEVELOPER;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
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
@WebMvcTest(value = AuthController.class, secure = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    private FieldDescriptor[] baseResposneDescription = new FieldDescriptor[]{
            fieldWithPath("status").description("status code"),
            fieldWithPath("errorCode").description("error code, 해당 코드를 보고 front 에서 분기처리를 한다"),
            fieldWithPath("data").description("respone data"),
    };

    private FieldDescriptor[] meResposneDescription = new FieldDescriptor[]{
            fieldWithPath("uuid").description("user uuid"),
            fieldWithPath("id").description("아이디"),
            fieldWithPath("name").description("user 이름"),
            fieldWithPath("nextersNumber").description("user 기수"),
            fieldWithPath("email").description("user email"),
            fieldWithPath("role").description("user 권한 {ROLE_ADMIN, ROLE_USER}"),
            fieldWithPath("position").description("user Position {DESIGNER, DEVELOPER}"),
            fieldWithPath("activated").description("user 활동 여부"),
            fieldWithPath("voteCount").description("user 아이디어 투표 횟수"),
            fieldWithPath("voted").description("user 투표 여부"),
            fieldWithPath("submitIdea").description("user 아이디어 제출 여부"),
            fieldWithPath("hasTeam").description("user 팀 소속 여부"),
            fieldWithPath("createdAt").description("user 가입 일자")
    };

    @BeforeEach
    void setUp() {
        user = new User("originman", "password1212", "kiwon",
                13, User.Role.ROLE_USER, DEVELOPER, "originman@nexters.com");
    }

    @Test
    void me() throws Exception {

        this.mockMvc.perform(get("/apis/me")
                .with(user(user)))
                .andExpect(status().isOk())
                .andDo(document("auth/get-me",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.", meResposneDescription)));
    }
}