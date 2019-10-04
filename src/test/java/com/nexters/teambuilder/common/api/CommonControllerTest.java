package com.nexters.teambuilder.common.api;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.teambuilder.common.Service.CommonService;
import com.nexters.teambuilder.common.api.dto.CommonRequest;
import com.nexters.teambuilder.common.api.dto.CommonResponse;
import com.nexters.teambuilder.common.domain.Common;
import com.nexters.teambuilder.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "${service.api-server}", uriPort = 80)
@WebMvcTest(value = CommonController.class, secure = false)
class CommonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommonService commonService;

    private ObjectMapper mapper;

    @Value("${service.api-server}")
    private String uriHost;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
    }

    private FieldDescriptor[] baseResposneDescription = new FieldDescriptor[]{
            fieldWithPath("status").description("status code"),
            fieldWithPath("errorCode").description("error code, 해당 코드를 보고 front 에서 분기처리를 한다"),
            fieldWithPath("data").description("response data"),
    };

    @Test
    void create() throws Exception {
        User user = new User("originman", "password1212", "kiwon",
                13, User.Role.ROLE_ADMIN, User.Position.DEVELOPER, "originman@nexters.com");

        Common common = new Common(1, 123456);

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("authenticationCode", "123456");

        given(commonService.createCommon(any(CommonRequest.class)))
                .willReturn(new CommonResponse(common.getId(), common.getAuthenticationCode()));

        this.mockMvc.perform(post("/apis/commons")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(input))
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").value(1))
                .andExpect(jsonPath("data.authenticationCode").value(123456))
                .andDo(document("commons/post-common",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("authenticationCode").description("회원 가입용 인증코드")
                        ),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.",
                                        fieldWithPath("id").description("id"),
                                        fieldWithPath("authenticationCode").description("회원 가입용 인증코드")
                                )));
    }

    @Test
    void getCommon() throws Exception {
        Common common = new Common(1, 123456);

        given(commonService.getCommon())
                .willReturn(new CommonResponse(common.getId(), common.getAuthenticationCode()));

        this.mockMvc.perform(get("/apis/commons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").value(1))
                .andExpect(jsonPath("data.authenticationCode").value(123456))
                .andDo(document("commons/get-common",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.",
                                        fieldWithPath("id").description("id"),
                                        fieldWithPath("authenticationCode").description("회원 가입용 인증코드")
                                )));
    }
}