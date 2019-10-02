package com.nexters.teambuilder.session.api;

import static com.nexters.teambuilder.idea.domain.Idea.Type.IDEA;
import static com.nexters.teambuilder.session.domain.Period.PeriodType.IDEA_COLLECT;
import static com.nexters.teambuilder.tag.domain.Tag.Type.DEVELOPER;
import static java.time.ZonedDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.idea.service.IdeaService;
import com.nexters.teambuilder.session.api.dto.SessionNumber;
import com.nexters.teambuilder.session.api.dto.SessionRequest;
import com.nexters.teambuilder.session.domain.Period;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.session.service.SessionService;
import com.nexters.teambuilder.tag.api.dto.TagResponse;
import com.nexters.teambuilder.tag.domain.Tag;
import com.nexters.teambuilder.tag.service.TagService;
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
@WebMvcTest(value = SessionController.class, secure = false)
class SessionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private TagService tagService;

    @MockBean
    private IdeaService ideaService;

    private ObjectMapper mapper;

    private User user;

    private Session session;

    private FieldDescriptor[] baseResposneDescription = new FieldDescriptor[]{
            fieldWithPath("status").description("status code"),
            fieldWithPath("errorCode").description("error code, 해당 코드를 보고 front 에서 분기처리를 한다"),
            fieldWithPath("data").description("respone data"),
    };

    private FieldDescriptor[] sessionRequestDescription = new FieldDescriptor[]{
            fieldWithPath("sessionNumber").description("기수 번호"),
            fieldWithPath("logoImageUrl").description("기수 로고 이미지 url"),
            fieldWithPath("teamBuildingMode").description("기수가 현재 팀빌딩 모드인지 아닌지 여부"),
            fieldWithPath("periods[]").description("기수의 운영 카테고리별 기간"),
            fieldWithPath("periods[].periodType").description("가수의 운영 카테고리 {IDEA_COLLECT, IDEA_VOTE, IDEA_CHECK, TEAM_BUILDING}"),
            fieldWithPath("periods[].startDate").description("각 항목별 기간 시작 날짜"),
            fieldWithPath("periods[].endDate").description("각 항목별 기간 끝나는 날짜"),
            fieldWithPath("maxVoteCount").description("respone data"),
    };

    private FieldDescriptor[] sessionResposneDescription = new FieldDescriptor[]{
            fieldWithPath("sessionId").description("session id"),
            fieldWithPath("sessionNumber").description("기수 번호"),
            fieldWithPath("sessionNumbers[]").description("전체 기수 번호들"),
            fieldWithPath("sessionNumbers[].sessionNumber").description("전체 기수번호"),
            fieldWithPath("logoImageUrl").description("기수 로고 이미지 url"),
            fieldWithPath("teamBuildingMode").description("팀빌딩 모드 여부"),
            fieldWithPath("periods[]").description("기수의 운영 카테고리별 기간"),
            fieldWithPath("periods[].periodType").description("가수의 운영 카테고리 {IDEA_COLLECT, IDEA_VOTE, IDEA_CHECK, TEAM_BUILDING}"),
            fieldWithPath("periods[].startDate").description("각 항목별 기간 시작날짜"),
            fieldWithPath("periods[].endDate").description("각 항목별 기간 끝나는 날짜"),
            fieldWithPath("periods[].now").description("현재 진행중인 카테고리인지 여부"),
            fieldWithPath("tags[]").description("tag 목록"),
            fieldWithPath("tags[].tagId").description("tag 아이디"),
            fieldWithPath("tags[].name").description("tag 이름"),
            fieldWithPath("tags[].type").description("tag 타입 {DEVELOPER, DESIGNER}"),
            fieldWithPath("maxVoteCount").description("최대 투표 가능 횟"),
            fieldWithPath("ideas[]").description("Idea 목록"),
            fieldWithPath("ideas[].ideaId").description("Idea id"),
            fieldWithPath("ideas[].sessionId").description("아이디어가 작성될 session(기수) id"),
            fieldWithPath("ideas[].title").description("아이디어 제목"),
            fieldWithPath("ideas[].content").description("아이디어 내용"),
            fieldWithPath("ideas[].author").description("작성자 정보"),
            fieldWithPath("ideas[].author").description("작성자 정보"),
            fieldWithPath("ideas[].author.uuid").description("user uuid"),
            fieldWithPath("ideas[].author.id").description("아이디"),
            fieldWithPath("ideas[].author.name").description("user 이름"),
            fieldWithPath("ideas[].author.nextersNumber").description("user 기수"),
            fieldWithPath("ideas[].author.email").description("user email"),
            fieldWithPath("ideas[].author.activated").description("user 활성화 여부"),
            fieldWithPath("ideas[].author.role").description("user 권한 {ROLE_ADMIN, ROLE_USER}"),
            fieldWithPath("ideas[].author.position").description("user Position {DESIGNER, DEVELOPER}"),
            fieldWithPath("ideas[].author.createdAt").description("user 가입 일자"),
            fieldWithPath("ideas[].tags[]").description("tag 목록"),
            fieldWithPath("ideas[].tags[].tagId").description("tag 목록"),
            fieldWithPath("ideas[].tags[].name").description("tag 이름"),
            fieldWithPath("ideas[].tags[].type").description("tag 타입 {DEVELOPER, DESIGNER}"),
            fieldWithPath("ideas[].file").description("첨부파일 url"),
            fieldWithPath("ideas[].type").description("아이디어 타입 {IDEA, NOTICE}"),
            fieldWithPath("ideas[].selected").description("아이디어 선정 여부"),
            fieldWithPath("ideas[].favorite").description("아이디어 즐겨찾기 여부"),
            fieldWithPath("ideas[].orderNumber").description("정렬 순서").type(NUMBER).optional(),
            fieldWithPath("ideas[].voteNumber").description("투표 수"),
            fieldWithPath("ideas[].createdAt").description("등록 시각"),
            fieldWithPath("ideas[].updatedAt").description("업데이트 시각"),

    };

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        user = new User("originman", "password1212", "kiwon",
                13, User.Role.ROLE_USER, User.Position.DEVELOPER, "originman@nexter.com");

        session = mock(Session.class);
        given(session.getSessionId()).willReturn(1);
        given(session.getSessionNumber()).willReturn(1);
        given(session.isTeamBuildingMode()).willReturn(false);
        given(session.getPeriods()).willReturn(Arrays.asList(new Period(IDEA_COLLECT, now(), now())));
        given(session.getLogoImageUrl()).willReturn("https://logo/image/url");
        given(session.getMaxVoteCount()).willReturn(1);
    }

    @Test
    void get_Session() throws Exception {
        List<TagResponse> tags = IntStream.range(1, 3).mapToObj(i -> new Tag("개발자", DEVELOPER))
                .map(TagResponse::of).collect(Collectors.toList());

        List<SessionNumber> sessionNumbers = IntStream.range(1, 3).mapToObj(i -> new SessionNumber(i))
                .collect(Collectors.toList());

        List<IdeaResponse> ideas = IntStream.range(1, 3).mapToObj(i -> new Idea(session, "모임모임 웹 서비스",
                "모임모임 같이만드실분 구합니다", user, "https://file.url",
                IDEA, Arrays.asList(new Tag("ios 개발자", DEVELOPER))))
                .map(IdeaResponse::of).collect(Collectors.toList());

        given(sessionService.getSession(anyInt())).willReturn(session);
        given(ideaService.getIdeaListBySessionId(anyInt(), any(User.class))).willReturn(ideas);
        given(sessionService.sessionNumberList()).willReturn(sessionNumbers);
        given(tagService.getTagList()).willReturn(tags);

        this.mockMvc.perform(get("/apis/sessions/{sessionNumber}", 1)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("sessions/get-session",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("sessionNumber").description("기수 번호")
                                        .attributes(key("constraints").value("Not Null"))),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.", sessionResposneDescription)
                ));
    }

    @Test
    void latest_Session() throws Exception {
        List<TagResponse> tags = IntStream.range(1, 3).mapToObj(i -> new Tag("개발자", DEVELOPER))
                .map(TagResponse::of).collect(Collectors.toList());

        List<SessionNumber> sessionNumbers = IntStream.range(1, 3).mapToObj(i -> new SessionNumber(i))
                .collect(Collectors.toList());

        List<IdeaResponse> ideas = IntStream.range(1, 3).mapToObj(i -> new Idea(session, "모임모임 웹 서비스",
                "모임모임 같이만드실분 구합니다", user, "https://file.url",
                IDEA, Arrays.asList(new Tag("ios 개발자", DEVELOPER))))
                .map(IdeaResponse::of).collect(Collectors.toList());

        given(sessionService.getLatestSession()).willReturn(session);
        given(ideaService.getIdeaListBySessionId(anyInt(), any(User.class))).willReturn(ideas);
        given(sessionService.sessionNumberList()).willReturn(sessionNumbers);
        given(tagService.getTagList()).willReturn(tags);

        this.mockMvc.perform(get("/apis/sessions/latest")
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("sessions/latest-session",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.", sessionResposneDescription)
                ));
    }

    @Test
    void create_Session() throws Exception {

        List<TagResponse> tags = IntStream.range(1, 3).mapToObj(i -> new Tag("개발자", DEVELOPER))
                .map(TagResponse::of).collect(Collectors.toList());

        List<SessionNumber> sessionNumbers = IntStream.range(1, 3).mapToObj(i -> new SessionNumber(i))
                .collect(Collectors.toList());

        List<IdeaResponse> ideas = IntStream.range(1, 3).mapToObj(i -> new Idea(session, "모임모임 웹 서비스",
                "모임모임 같이만드실분 구합니다", user, "https://file.url",
                IDEA, Arrays.asList(new Tag("ios 개발자", DEVELOPER))))
                .map(IdeaResponse::of).collect(Collectors.toList());


        given(sessionService.createSession(any(SessionRequest.class))).willReturn(session);
        given(ideaService.getIdeaListBySessionId(anyInt(), any(User.class))).willReturn(ideas);
        given(sessionService.sessionNumberList()).willReturn(sessionNumbers);
        given(tagService.getTagList()).willReturn(tags);

        Map<String, Object> period = new LinkedHashMap<>();
        period.put("periodType", IDEA_COLLECT);
        period.put("startDate", now().toOffsetDateTime().toString());
        period.put("endDate", now().toOffsetDateTime().toString());

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("sessionNumber", 1);
        input.put("logoImageUrl", "https://logo/image.url");
        input.put("teamBuildingMode", false);
        input.put("periods", Arrays.asList(period));
        input.put("maxVoteCount", 3);

        this.mockMvc.perform(post("/apis/sessions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(input))
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("sessions/post-session",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(sessionRequestDescription),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.", sessionResposneDescription)
                ));
    }

    @Test
    void update_Session() throws Exception {

        List<TagResponse> tags = IntStream.range(1, 3).mapToObj(i -> new Tag("개발자", DEVELOPER))
                .map(TagResponse::of).collect(Collectors.toList());

        List<SessionNumber> sessionNumbers = IntStream.range(1, 3).mapToObj(i -> new SessionNumber(i))
                .collect(Collectors.toList());

        List<IdeaResponse> ideas = IntStream.range(1, 3).mapToObj(i -> new Idea(session, "모임모임 웹 서비스",
                "모임모임 같이만드실분 구합니다", user, "https://file.url",
                IDEA, Arrays.asList(new Tag("ios 개발자", DEVELOPER))))
                .map(IdeaResponse::of).collect(Collectors.toList());


        given(sessionService.updateSession(anyInt(), any(SessionRequest.class))).willReturn(session);
        given(ideaService.getIdeaListBySessionId(anyInt(), any(User.class))).willReturn(ideas);
        given(sessionService.sessionNumberList()).willReturn(sessionNumbers);
        given(tagService.getTagList()).willReturn(tags);

        Map<String, Object> period = new LinkedHashMap<>();
        period.put("periodType", IDEA_COLLECT);
        period.put("startDate", now().toOffsetDateTime().toString());
        period.put("endDate", now().toOffsetDateTime().toString());

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("sessionNumber", 1);
        input.put("logoImageUrl", "https://logo/image.url");
        input.put("teamBuildingMode", false);
        input.put("periods", Arrays.asList(period));
        input.put("maxVoteCount", 3);

        this.mockMvc.perform(put("/apis/sessions/{sessionNumber}", 1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(input))
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("sessions/update-session",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("sessionNumber").description("기수 번호")
                                        .attributes(key("constraints").value("Not Null"))),
                        requestFields(sessionRequestDescription),
                        responseFields(baseResposneDescription)
                                .andWithPrefix("data.", sessionResposneDescription)
                ));
    }
}