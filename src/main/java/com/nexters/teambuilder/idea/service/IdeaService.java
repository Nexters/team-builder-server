package com.nexters.teambuilder.idea.service;

import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.idea.domain.IdeaRepository;
import com.nexters.teambuilder.idea.exception.IdeaNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class IdeaService {
    private final IdeaRepository ideaRepository;

    public IdeaResponse createIdea(IdeaRequest request) {
        return IdeaResponse.of(ideaRepository.save(Idea.of(request)));
    }

    public IdeaResponse getIdea(Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));
        return IdeaResponse.of(idea);
    }

    public IdeaResponse updateIdea(Integer ideaId, IdeaRequest request) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));

        idea.update(request);

        return IdeaResponse.of(ideaRepository.save(idea));
    }

    public List<IdeaResponse> getIdeaList() {
        List<Idea> ideaList = ideaRepository.findAll();
        return ideaList.stream().map(IdeaResponse::of).collect(Collectors.toList() );
    }


}
