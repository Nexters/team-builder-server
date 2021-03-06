package com.nexters.teambuilder.idea.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.api.dto.MemberRequest;
import com.nexters.teambuilder.idea.api.dto.MemberResponse;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.idea.domain.Member;
import com.nexters.teambuilder.idea.exception.UserHasTeamException;
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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.nexters.teambuilder.idea.domain.Idea.Type.IDEA;
import static com.nexters.teambuilder.session.domain.Period.PeriodType.IDEA_COLLECT;
import static com.nexters.teambuilder.tag.domain.Tag.Type.DEVELOPER;
import static java.time.ZonedDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private Member member;

    private ObjectMapper mapper;

    private FieldDescriptor[] baseResponseDescription = new FieldDescriptor[]{
            fieldWithPath("status").description("status code"),
            fieldWithPath("errorCode").description("error code, 해당 코드를 보고 front 에서 분기처리를 한다"),
            fieldWithPath("data").description("response data"),
    };

    private FieldDescriptor[] ideaRequestDescription = new FieldDescriptor[]{
            fieldWithPath("sessionId").description("아이디어가 작성될 session(기수) id"),
            fieldWithPath("title").description("아이디어 제목"),
            fieldWithPath("content").description("아이디어 내용"),
            fieldWithPath("tags").description("tag Id 목록"),
            fieldWithPath("file").description("첨부파일 url"),
            fieldWithPath("type").description("아이디어 타입 {IDEA, NOTICE}"),
            fieldWithPath("selected").description("아이디어 선정 여부"),
    };

    private FieldDescriptor[] addMemberDescription = new FieldDescriptor[]{
            fieldWithPath("uuid").description("회원의 uuid"),
            fieldWithPath("id").description("회원의 id"),
            fieldWithPath("name").description("회원의 이름"),
            fieldWithPath("nextersNumber").description("회원의 기수"),
            fieldWithPath("position").description("회원의 직군"),
            fieldWithPath("hasTeam").description("회원의 팀 빌딩 여부"),
    };

    private FieldDescriptor[] userHasTeamException = new FieldDescriptor[]{
            fieldWithPath("id").description("중복된 회원의 id"),
            fieldWithPath("uuid").description("중복된 회원의 uuid"),
            fieldWithPath("name").description("중복된 회원의 이름"),
            fieldWithPath("nextersNumber").description("중복된 회원의 기수"),
            fieldWithPath("position").description("중복된 회원의 직군"),
            fieldWithPath("hasTeam").description("중복된 회원의 팀 빌딩 여부"),
    };

    private FieldDescriptor[] ideaResponseDescription = new FieldDescriptor[]{
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
            fieldWithPath("members").description("팀 멤버 목록"),
            fieldWithPath("members[].uuid").description("팀 멤버 uuid"),
            fieldWithPath("members[].id").description("팀 멤버 id"),
            fieldWithPath("members[].name").description("팀 멤버 name"),
            fieldWithPath("members[].nextersNumber").description("팀 멤버 넥스터즈 기수"),
            fieldWithPath("members[].position").description("팀 멤버 포지션 {DESIGNER, DEVELOPER}"),
            fieldWithPath("members[].hasTeam").description("팀 멤버가 현재 '활동'중인 기수에서 팀에 소속되어있는지 여부"),

    };

    @BeforeEach
    void setUp() {
        user = new User("originman", "password1212", "kiwon",
                13, User.Role.ROLE_USER, User.Position.DEVELOPER, "originman@nexter.com");

        session = new Session(1, false,
                Arrays.asList(new Period(IDEA_COLLECT, now(), now())), "https://logo/image/url", 1);

        idea = new Idea(session, "모임모임 웹 서비스", "모임모임 같이만드실분 구합니다", user, "https://file.url", IDEA,
                Arrays.asList(new Tag("ios 개발자", DEVELOPER)));

        member = new Member("uuid", user.getId(), user.getName(), user.getNextersNumber(),
                user.getPosition(), false);

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
                        requestFields(ideaRequestDescription),
                        responseFields(baseResponseDescription)
                                .andWithPrefix("data.", ideaResponseDescription)));
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
                        responseFields(baseResponseDescription)
                                .andWithPrefix("data.", ideaResponseDescription)));
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
                        responseFields(baseResponseDescription)
                                .andWithPrefix("data.[].", ideaResponseDescription)));
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
                        requestFields(ideaRequestDescription),
                        responseFields(baseResponseDescription)
                                .andWithPrefix("data.", ideaResponseDescription)));
    }

    @Test
    void delete_Idea() throws Exception {
        this.mockMvc.perform(delete("/apis/ideas/{ideaId}", 1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/delete-idea",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("ideaId").description("Idea의 id")
                                        .attributes(key("constraints").value("Not Null")))
                ));
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
                        responseFields(baseResponseDescription)));
    }

    @Test
    void vote_Ideas() throws Exception {

        this.mockMvc.perform(put("/apis/ideas/vote?ideaIds=1,2,3,4,5,6,7,8,9,10")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/vote-ideas",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("ideaIds").description("Idea의 id list")
                                        .attributes(key("constraints").value("Not empty"))),
                        responseFields(baseResponseDescription)));
    }

    @Test
    void addMember() throws Exception {
        List<User> members = IntStream.range(1, 5).mapToObj(i -> {
            user.updateHasTeam(true);
            user.setUuid("uuid");
            return user;
        }).collect(Collectors.toList());

        List<MemberResponse> newMembers = members.stream()
                .map(MemberResponse::createMemberFrom).collect(Collectors.toList());

        given(ideaService.addMember(any(User.class), anyInt(), any(MemberRequest.class)))
                .willReturn(newMembers);

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("uuids", Arrays.asList("uuid", "uuid"));

        this.mockMvc.perform(put("/apis/ideas/{ideaId}/team", 1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(input))
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/team",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())
                        , requestFields(fieldWithPath("uuids").description("user의 uuid. " +
                                "다른 팀에 이미 속해 있을 경우 error 발생(error code 90005).")),
                        responseFields(baseResponseDescription)
                                .andWithPrefix("data[].",addMemberDescription)));
    }

    @Test
    void userHasTeamThrowsException() throws Exception {
        List<User> members = IntStream.range(1, 5).mapToObj(i -> {
            user.updateHasTeam(true);
            user.setUuid("uuid");
            return user;
        }).collect(Collectors.toList());

        List<MemberResponse> newMembers = members.stream()
                .map(MemberResponse::createMemberFrom).collect(Collectors.toList());

        given(ideaService.addMember(any(User.class), anyInt(), any(MemberRequest.class)))
                .willThrow(new UserHasTeamException(newMembers));

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("uuids", Arrays.asList(member.getUuid(), member.getUuid()));

        this.mockMvc.perform(put("/apis/ideas/{ideaId}/team", 1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(input))
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isBadRequest())
                .andDo(document("ideas/team-exception",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())
                        , requestFields(fieldWithPath("uuids").description("user의 uuid. " +
                                "다른 팀에 이미 속해 있을 경우 error 발생(error code 90005).")),
                        responseFields( fieldWithPath("status").description("status code"),
                                fieldWithPath("errorCode").description("error code"),
                                fieldWithPath("error").description("error 이름"),
                                fieldWithPath("message").description("error 메시지"),
                                fieldWithPath("timestamp").description("발생 시각"),
                                fieldWithPath("hasTeamMembers").description("중복된 회원"))
                                .andWithPrefix("hasTeamMembers[].", userHasTeamException)));
    }

    @Test
    void select() throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("ideaIds", Arrays.asList(1, 2));

        this.mockMvc.perform(put("/apis/ideas/select")
                .content(mapper.writeValueAsString(input))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/put-select",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Bearer oAuth2 access_token,"
                                                + " admin계정이 아닐경우 error 발생 error code : 90007")),
                        requestFields(
                                fieldWithPath("ideaIds")
                                        .description("선정하려는 아이디어들의 id목록")),
                        responseFields(baseResponseDescription)));
    }

    @Test
    void deselect() throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("ideaIds", Arrays.asList(1, 2));

        this.mockMvc.perform(put("/apis/ideas/deselect")
                .content(mapper.writeValueAsString(input))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + "<access_token>"))
                .andExpect(status().isOk())
                .andDo(document("ideas/put-deselect",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Bearer oAuth2 access_token,"
                                                + " admin계정이 아닐경우 error 발생 error code : 90007")),
                        requestFields(
                                fieldWithPath("ideaIds")
                                        .description("선정 해제하려는 아이디어들의 id목록")),
                        responseFields(baseResponseDescription)));
    }
}
