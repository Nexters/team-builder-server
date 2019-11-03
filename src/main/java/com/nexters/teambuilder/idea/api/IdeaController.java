package com.nexters.teambuilder.idea.api;

import com.fasterxml.jackson.annotation.JsonView;
import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.common.view.Views;
import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.api.dto.MemberRequest;
import com.nexters.teambuilder.idea.api.dto.MemberResponse;
import com.nexters.teambuilder.idea.service.IdeaService;
import com.nexters.teambuilder.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/apis/ideas")
public class IdeaController {
    private final IdeaService ideaService;

    @GetMapping
    public BaseResponse<List<IdeaResponse>> list(@AuthenticationPrincipal User user) {
        List<IdeaResponse> ideas = ideaService.getIdeaList(user);
        return new BaseResponse<>(200, 0, ideas);
    }

    @PostMapping
    public BaseResponse<IdeaResponse> create(@AuthenticationPrincipal User user,
                                             @RequestBody @Valid IdeaRequest request){
        IdeaResponse idea = ideaService.createIdea(user, request);
        return new BaseResponse<>(200, 0, idea);
    }

    @GetMapping("/{ideaId}")
    @JsonView(Views.External.class)
    public BaseResponse<IdeaResponse> get(@AuthenticationPrincipal User user,
                                          @PathVariable Integer ideaId){
       IdeaResponse idea = ideaService.getIdea(user, ideaId);
        return new BaseResponse<>(200, 0, idea);
    }

    @PutMapping("/{ideaId}")
    public BaseResponse<IdeaResponse> update(@AuthenticationPrincipal User user,
                                             @PathVariable Integer ideaId,
                                             @RequestBody @Valid IdeaRequest request){
        IdeaResponse idea = ideaService.updateIdea(user, ideaId, request);
        return new BaseResponse<>(200, 0, idea);
    }

    @DeleteMapping("/{ideaId}")
    public BaseResponse delete(@AuthenticationPrincipal User user, @PathVariable Integer ideaId){
        ideaService.deleteIdea(user, ideaId);
        return new BaseResponse<>(200, 0, null);
    }

    @PutMapping("/{ideaId}/vote")
    public BaseResponse vote(@AuthenticationPrincipal User user, @PathVariable Integer ideaId){
        ideaService.ideaVote(user, ideaId);
        return new BaseResponse<>(200, 0, null);
    }

    @PutMapping("/vote")
    public BaseResponse voteIdeas(@AuthenticationPrincipal User user, @RequestParam List<Integer> ideaIds){
        ideaService.ideasVote(user, ideaIds);
        return new BaseResponse<>(200, 0, null);
    }

    @PutMapping("/{ideaId}/team")
    public BaseResponse addMember(@AuthenticationPrincipal User user, @PathVariable Integer ideaId,
                                 @RequestBody MemberRequest request){
        List<MemberResponse> members = ideaService.addMember(ideaId, request);
        return new BaseResponse<>(200, 0, members);
    }
}
