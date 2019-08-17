package com.nexters.teambuilder.session.api;

import java.util.List;

import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.service.IdeaService;
import com.nexters.teambuilder.session.api.dto.SessionNumber;
import com.nexters.teambuilder.session.api.dto.SessionRequest;
import com.nexters.teambuilder.session.api.dto.SessionResponse;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.session.service.SessionService;
import com.nexters.teambuilder.tag.api.dto.TagResponse;
import com.nexters.teambuilder.tag.service.TagService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apis/sessions")
public class SessionController {
    private final SessionService sessionService;
    private final TagService tagService;
    private final IdeaService ideaService;

    @GetMapping("{sessionNumber}")
    public BaseResponse<SessionResponse> get(@PathVariable Integer sessionNumber) {
        Session session = sessionService.getSession(sessionNumber);
        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaList();
        List<SessionNumber>  sessionNumbers = sessionService.sessionNumberList();
        return new BaseResponse<>(200, 0, SessionResponse.of(session, sessionNumbers, tags, ideas));
    }

    @ApiOperation(value = "기수 생성")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enum type 설명 : periods.periodType", value = "{IDEA_COLLECT or IDEA_VOTE or IDEA_CHECK or TEAM_BUILDING}", required = true, dataType = "date", paramType = "body"),
    })
    @PostMapping
    public BaseResponse<SessionResponse> create(@RequestBody SessionRequest request) {
        Session session = sessionService.createSession(request);
        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaList();
        List<SessionNumber>  sessionNumbers = sessionService.sessionNumberList();
        return new BaseResponse<>(200, 0, SessionResponse.of(session, sessionNumbers, tags, ideas));
    }

    @ApiOperation(value = "기수 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enum type 설명 : periods.periodType", value = "{IDEA_COLLECT or IDEA_VOTE or IDEA_CHECK or TEAM_BUILDING}", required = true, dataType = "date", paramType = "body"),
    })
    @PutMapping("{sessionNumber}")
    public BaseResponse<SessionResponse> update(@PathVariable Integer sessionNumber, @RequestBody SessionRequest request) {
        Session session = sessionService.updateSession(sessionNumber, request);

        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaList();
        List<SessionNumber>  sessionNumbers = sessionService.sessionNumberList();
        return new BaseResponse<>(200, 0, SessionResponse.of(session, sessionNumbers, tags, ideas));
    }
}
