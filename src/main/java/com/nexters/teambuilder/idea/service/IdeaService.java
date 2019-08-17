package com.nexters.teambuilder.idea.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.idea.domain.IdeaRepository;
import com.nexters.teambuilder.idea.exception.IdeaNotFoundException;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.tag.domain.Tag;
import com.nexters.teambuilder.tag.domain.TagRepository;
import com.nexters.teambuilder.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class IdeaService {
    private final IdeaRepository ideaRepository;
    private final TagRepository tagRepository;

    public IdeaResponse createIdea(User author, IdeaRequest request) {
        List<Tag> tags = tagRepository.findAllById(request.getTags());
        return IdeaResponse.of(ideaRepository.save(Idea.of(author, tags, request)));
    }

    public IdeaResponse getIdea(Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));
        return IdeaResponse.of(idea);
    }

    public IdeaResponse updateIdea(User author, Integer ideaId, IdeaRequest request) {

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));

        if(!idea.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("해당 아이디어의 작성자가 아닙니다");
        }

        idea.update(request);

        return IdeaResponse.of(ideaRepository.save(idea));
    }

    public List<IdeaResponse> getIdeaList() {
        List<Idea> ideaList = ideaRepository.findAll();
        return ideaList.stream()
                .sorted(Comparator.comparing(Idea::getIdeaId).reversed())
                .map(idea -> {
            IdeaResponse ideaResponse = IdeaResponse.of(idea);
            ideaResponse.setOrderNumber(ideaList.indexOf(idea) + 1);
            return ideaResponse;
        }).collect(Collectors.toList());
//        return ideaList.stream().map(IdeaResponse::of).collect(Collectors.toList());
    }

    public void deleteIdea(User author, Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));

        if(!idea.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("해당 아이디어의 작성자가 아닙니다");
        }

        ideaRepository.delete(idea);
    }


}
