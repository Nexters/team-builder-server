package com.nexters.teambuilder.user.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.teambuilder.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "${service.api-server}", uriPort = 80)
@WebMvcTest(value = AuthController.class, secure = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private User user;

    private User principal;

    @BeforeEach
    void setUp() {
        user = new User("originman", "password1212", "kiwon",
                13, User.Role.ROLE_USER, User.Position.DEVELOPER);
        mapper = new ObjectMapper();
    }

    @Test
    void me_GivenToken_GetUserInfo() throws Exception {

        this.mockMvc.perform(get("/apis/me")
                .with(user(user)))
                .andExpect(status().isOk())
                .andDo(document("auth/get-me",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("uuid").description("user uuid"),
                                fieldWithPath("id").description("아이디"),
                                fieldWithPath("name").description("user 이름"),
                                fieldWithPath("term").description("user 기수"),
                                fieldWithPath("role").description("user 권한 {ROLE_ADMIN, ROLE_USER}"),
                                fieldWithPath("position").description("user Position {DESIGNER, DEVELOPER}"),
                                fieldWithPath("createdAt").description("user 가입 일자")
                        )));
    }
}