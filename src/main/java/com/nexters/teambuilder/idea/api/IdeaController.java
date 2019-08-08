package com.nexters.teambuilder.idea.api;

import com.nexters.teambuilder.common.response.BaseResponse;
import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.service.IdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ideas")
public class IdeaController {
    private final IdeaService ideaService;

    @GetMapping
    public BaseResponse<List<IdeaResponse>> list() {
        List<IdeaResponse> ideas = ideaService.getIdeaList();
        return new BaseResponse<>(200, 0, ideas);
    }

    @PostMapping
    public BaseResponse<IdeaResponse> create(@RequestBody @Valid IdeaRequest request){
        IdeaResponse idea = ideaService.createIdea(request);
        return new BaseResponse<>(200, 0, idea);
    }

    @GetMapping("/{ideaId}")
    public BaseResponse<IdeaResponse> get(@PathVariable Integer ideaId){
       IdeaResponse idea = ideaService.getIdea(ideaId);
        return new BaseResponse<>(200, 0, idea);
    }

    @PutMapping("/{ideaId}")
    public BaseResponse<IdeaResponse> update(@PathVariable Integer ideaId,
                               @RequestBody @Valid IdeaRequest request){
        IdeaResponse idea = ideaService.updateIdea(ideaId, request);
        return new BaseResponse<>(200, 0, idea);
    }

    @DeleteMapping("/{ideaId}")
    public BaseResponse delete(@PathVariable Integer ideaId){
        ideaService.deleteIdea(ideaId);
        return new BaseResponse<>(200, 0, null);
    }


}
