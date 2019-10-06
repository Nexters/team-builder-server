package com.nexters.teambuilder.idea.api;

import static com.nexters.teambuilder.idea.domain.Idea.Type.IDEA;
import static com.nexters.teambuilder.session.domain.Period.PeriodType.IDEA_COLLECT;
import static com.nexters.teambuilder.tag.domain.Tag.Type.DEVELOPER;
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
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.idea.service.IdeaService;
import com.nexters.teambuilder.session.domain.Period;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.tag.domain.Tag;
import com.nexters.teambuilder.user.domain.User;
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
@WebMvcTest(value = IdeaController.class, secure = false)
class IdeaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdeaService ideaService;

    private Idea idea;

    private Session session;

    private User user;

    private ObjectMapper mapper;

    private FieldDescriptor[] baseResposneDescription = new FieldDescriptor[]{
            fieldWithPath("status").description("status code"),
            fieldWithPath("errorCode").description("error code, 해당 코드를 보고 front 에서 분기처리를 한다"),
            fieldWithPath("data").description("respone data"),
    };

    private FieldDescriptor[] ideaRequesteDescription = new FieldDescriptor[]{
            fieldWithPath("sessionId").description("아이디어가 작성될 session(기수) id"),
            fieldWithPath("title").description("아이디어 제목"),
            fieldWithPath("content").description("아이디어 내용"),
            fieldWithPath("tags").description("tag Id 목록"),
            fieldWithPath("file").description("첨부파일 url"),
            fieldWithPath("type").description("아이디어 타입 {IDEA, NOTICE}"),
            fieldWithPath("selected").description("아이디어 선정 여부"),
    };

    private FieldDescriptor[] ideaResposneDescription = new FieldDescriptor[] {
            fieldWithPath("ideaId").description("id"),
            fieldWithPath("sessionId").description("아이디어가 작성될 session(기수) id"),
            fieldWithPath("title").description("아이디어 제목"),
            fieldWithPath("content").description("아이디어 내용"),
            fieldWithPath("author").description("작성자 정보"),
            fieldWithPath("author").description("작성자 정보"),
            fieldWithPath("author.uuid").description("user uuid"),
            fieldWithPath("author.id").description("아이디"),
            fieldWithPath("author.name").description("user 이름"),
            fieldWithPath("author.nextersNumber").description("user 기수"),
            fieldWithPath("author.email").description("user email"),
            fieldWithPath("author.role").description("user 권한 {ROLE_ADMIN, ROLE_USER}"),
            fieldWithPath("author.position").description("user Position {DESIGNER, DEVELOPER}"),
            fieldWithPath("author.activated").description("user 활성화 여부"),
            fieldWithPath("author.voteCount").description("user 아이디어 투표 횟수"),
            fieldWithPath("author.voted").description("user 투표 여부"),
            fieldWithPath("author.submitIdea").description("user 아이디어 제출 여부"),
            fieldWithPath("author.hasTeam").description("user 팀 소속 여부"),
            fieldWithPath("author.createdAt").description("user 가입 일자"),
            fieldWithPath("tags[]").description("tag 목록"),
            fieldWithPath("tags[].tagId").description("tag 목록"),
            fieldWithPath("tags[].name").description("tag 이름"),
            fieldWithPath("tags[].type").description("tag 타입 {DEVELOPER, DESIGNER}"),
            fieldWithPath("file").description("첨부파일 url"),
            fieldWithPath("type").description("아이디어 타입 {IDEA, NOTICE}"),
            fieldWithPath("selected").description("아이디어 선정 여부"),
            fieldWithPath("favorite").description("아이디어 즐겨찾기 여부"),
            fieldWithPath("orderNumber").description("정렬 순서").type(NUMBER).optional(),
            fieldWithPath("voteNumber").description("투표 수"),
            fieldWithPath("createdAt").description("등록 시각"),
            fieldWithPath("updatedAt").description("업데이트 시각"),
    };

    @BeforeEach
    void setUp() {
        user = new User("originman", "password1212", "kiwon",
                13, User.Role.ROLE_USER, User.Position.DEVELOPER, "originman@nexter.com");

        session = new Session(1, false,
                Arrays.asList(new Period(IDEA_COLLECT, now(), now())), "https://logo/image/url", 1);

        idea = new Idea(session, "모임모임 웹 서비스", "모임모임 같이만드실분 구합니다", user, "https://file.url", IDEA,
                Arrays.asList(new Tag("ios 개발자", DEVELOPER)));

        mapper = new ObjectMapper();
    }

    @Test
    void create_Idea() throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("sessionId", 1);
        input.put("title", "모임모임 웹 서비스");
        input.put("content", "모임모임 같이만드실분 구합니다");
        input.put("tags", Arrays.asList(1, 2));
        input.put("file", "https://file.url");
        input.put("type", IDEA);
        input.put("selected", false);

        given(ideaService.createIdea(any(User.class), any(IdeaRequest.class))).willReturn(IdeaResponse.of(idea));

        this.mockMvc.perform(post("/apis/ideas")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(input))
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/post-idea",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(ideaRequesteDescription),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.", ideaResposneDescription)));
    }

    @Test
    void get_Idea() throws Exception {
        given(ideaService.getIdea(any(User.class), anyInt())).willReturn(IdeaResponse.of(idea));

        this.mockMvc.perform(get("/apis/ideas/{ideaId}", 1)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/get-idea",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("ideaId").description("Idea의 id")
                                        .attributes(key("constraints").value("Not Null"))),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.", ideaResposneDescription)));
    }

    @Test
    void list_Idea() throws Exception {
        List<IdeaResponse> ideas = IntStream.range(1, 3)
                .mapToObj(i -> new Idea(session, "모임모임 웹 서비스", "모임모임 같이만드실분 구합니다",
                        user, "https://file.url", IDEA, Arrays.asList(new Tag("ios 개발자", DEVELOPER))))
                .map(IdeaResponse::of)
                .collect(Collectors.toList());

        given(ideaService.getIdeaList(any(User.class))).willReturn(ideas);

        this.mockMvc.perform(get("/apis/ideas")
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/list-idea",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.[].", ideaResposneDescription)));
    }

    @Test
    void update_Idea() throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("sessionId", 1);
        input.put("title", "모임모임 웹 서비스");
        input.put("content", "모임모임 같이만드실분 구합니다");
        input.put("tags", Arrays.asList(1, 2));
        input.put("file", "https://file.url");
        input.put("type", IDEA);
        input.put("selected", false);

        given(ideaService.updateIdea(any(User.class), anyInt(), any(IdeaRequest.class))).willReturn(IdeaResponse.of(idea));

        this.mockMvc.perform(put("/apis/ideas/{ideaId}", 1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(input))
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/put-idea",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("ideaId").description("Idea의 id")
                                        .attributes(key("constraints").value("Not Null"))),
                        requestFields(ideaRequesteDescription),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.", ideaResposneDescription)));
    }

    @Test
    void vote_Idea() throws Exception {
        this.mockMvc.perform(put("/apis/ideas/{ideaId}/vote", 1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/vote-idea",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("ideaId").description("Idea의 id")
                                        .attributes(key("constraints").value("Not Null"))),
                        responseFields(baseResposneDescription)));
    }
}