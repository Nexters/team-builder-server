package com.nexters.teambuilder.session.api;

import java.util.List;

import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.service.IdeaService;
import com.nexters.teambuilder.session.api.dto.SessionRequest;
import com.nexters.teambuilder.session.api.dto.SessionResponse;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.session.service.SessionService;
import com.nexters.teambuilder.tag.api.dto.TagResponse;
import com.nexters.teambuilder.tag.service.TagService;
import com.nexters.teambuilder.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("{sessionId}")
    public BaseResponse<SessionResponse> get(@AuthenticationPrincipal User user, @PathVariable Integer sessionId) {
        Session session = sessionService.getSession(sessionId);
        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaList();
        return new BaseResponse<>(200, 0, SessionResponse.of(session, tags, ideas));
    }

    @PostMapping
    public BaseResponse<SessionResponse> create(@AuthenticationPrincipal User user, @RequestBody SessionRequest request) {
        Session session = sessionService.createSession(request);
        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaList();
        return new BaseResponse<>(200, 0, SessionResponse.of(session, tags, ideas));
    }

    @PutMapping("{sessionId}")
    public BaseResponse<SessionResponse> update(@PathVariable Integer sessionId, @RequestBody SessionRequest request) {
        Session session = sessionService.updateSession(sessionId, request);

        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaList();
        return new BaseResponse<>(200, 0, SessionResponse.of(session, tags, ideas));
    }
}
