package com.nexters.teambuilder.session.api;

import java.util.List;

import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.api.dto.VotedIdeaResponse;
import com.nexters.teambuilder.idea.service.IdeaService;
import com.nexters.teambuilder.session.api.dto.SessionNumber;
import com.nexters.teambuilder.session.api.dto.SessionRequest;
import com.nexters.teambuilder.session.api.dto.SessionResponse;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.session.service.SessionService;
import com.nexters.teambuilder.tag.api.dto.TagResponse;
import com.nexters.teambuilder.tag.service.TagService;
import com.nexters.teambuilder.user.api.dto.SessionUserResponse;
import com.nexters.teambuilder.user.domain.User;
import javafx.geometry.VPos;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/apis/sessions")
public class SessionController {
    private final SessionService sessionService;
    private final TagService tagService;
    private final IdeaService ideaService;

    @GetMapping("{sessionNumber}")
    public BaseResponse<SessionResponse> get(@AuthenticationPrincipal User user, @PathVariable Integer sessionNumber) {
        Session session = sessionService.getSession(sessionNumber);
        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaListBySessionId(session.getSessionId(), user);
        List<VotedIdeaResponse> votedIdeas = ideaService.votedIdeas(user, sessionNumber);
        List<SessionNumber> sessionNumbers = sessionService.sessionNumberList();

        return new BaseResponse<>(200, 0,
                SessionResponse.of(session, sessionNumbers, tags, ideas, votedIdeas));
    }

    @GetMapping("latest")
    public BaseResponse<SessionResponse> getLatest(@AuthenticationPrincipal User user) {
        Session session = sessionService.getLatestSession();
        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaListBySessionId(session.getSessionId(), user);
        List<VotedIdeaResponse> votedIdeas = ideaService.votedIdeas(user, session.getSessionNumber());
        List<SessionNumber>  sessionNumbers = sessionService.sessionNumberList();
        return new BaseResponse<>(200, 0, SessionResponse.of(session, sessionNumbers, tags, ideas, votedIdeas));
    }

    @PostMapping
    public BaseResponse<SessionResponse> create(@AuthenticationPrincipal User user, @RequestBody SessionRequest request) {
        Session session = sessionService.createSession(request);
        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaListBySessionId(session.getSessionId(), user);
        List<VotedIdeaResponse> votedIdeas = ideaService.votedIdeas(user, session.getSessionNumber());
        List<SessionNumber>  sessionNumbers = sessionService.sessionNumberList();
        return new BaseResponse<>(200, 0,
                SessionResponse.of(session, sessionNumbers, tags, ideas, votedIdeas));
    }

    @PutMapping("{sessionNumber}")
    public BaseResponse<SessionResponse> update(@AuthenticationPrincipal User user, @PathVariable Integer sessionNumber, @RequestBody SessionRequest request) {
        Session session = sessionService.updateSession(sessionNumber, request);

        List<TagResponse> tags = tagService.getTagList();
        List<IdeaResponse> ideas = ideaService.getIdeaListBySessionId(session.getSessionId(), user);
        List<VotedIdeaResponse> votedIdeas = ideaService.votedIdeas(user, sessionNumber);
        List<SessionNumber>  sessionNumbers = sessionService.sessionNumberList();
        return new BaseResponse<>(200, 0,
                SessionResponse.of(session, sessionNumbers, tags, ideas, votedIdeas));
    }

    @DeleteMapping("{sessionNumber}")
    public BaseResponse delete(@AuthenticationPrincipal User user, @PathVariable Integer sessionNumber) {
        sessionService.deleteSession(sessionNumber, user);
        return new BaseResponse<>(200, 0, null);
    }

    @PutMapping("/{sessionNumber}/add-users")
    public BaseResponse<List<SessionUserResponse>> addSessionUser(@PathVariable Integer sessionNumber,
                                                                  @RequestParam List<String> uuids) {
        List<SessionUserResponse> sessionUsers = sessionService.addSessionUsers(sessionNumber, uuids);
        return new BaseResponse<>(200, 0, sessionUsers);
    }

    @PutMapping("{sessionNumber}/delete-users")
    public BaseResponse<List<SessionUserResponse>> deleteSessionUser(@PathVariable Integer sessionNumber,
                                                                  @RequestParam List<String> uuids) {
        List<SessionUserResponse> sessionUsers = sessionService.deleteSessionUsers(sessionNumber, uuids);
        return new BaseResponse<>(200, 0, sessionUsers);
    }
}
