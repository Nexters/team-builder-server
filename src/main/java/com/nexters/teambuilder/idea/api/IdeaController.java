package com.nexters.teambuilder.idea.api;

import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.idea.domain.IdeaRepository;
import com.nexters.teambuilder.idea.exception.IdeaNotFoundException;
import com.nexters.teambuilder.idea.service.IdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("idea")
public class IdeaController {
    private final IdeaService ideaService;

    @GetMapping("list")
    public List<IdeaResponse> ideaList() {
        return ideaService.getIdeaList();
    }

    @GetMapping("{ideaId}")
    public IdeaResponse getIdea(@PathVariable Integer ideaId){
       return ideaService.getIdea(ideaId);
    }

    @PostMapping
    public IdeaResponse create(@RequestBody @Valid IdeaRequest request){
        return ideaService.createIdea(request);
    }

    @PutMapping("{ideaId}")
    public IdeaResponse update(@PathVariable Integer ideaId,
                               @RequestBody @Valid IdeaRequest request){
        return ideaService.updateIdea(ideaId, request);
    }


}
