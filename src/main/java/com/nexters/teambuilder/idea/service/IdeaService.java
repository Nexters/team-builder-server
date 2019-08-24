package com.nexters.teambuilder.idea.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.idea.domain.IdeaRepository;
import com.nexters.teambuilder.idea.exception.IdeaNotFoundException;
import com.nexters.teambuilder.idea.exception.NotHasRightVoteException;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.session.domain.SessionRepository;
import com.nexters.teambuilder.session.domain.SessionUser;
import com.nexters.teambuilder.session.exception.SessionNotFoundException;
import com.nexters.teambuilder.tag.domain.Tag;
import com.nexters.teambuilder.tag.domain.TagRepository;
import com.nexters.teambuilder.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springfox.documentation.service.Tags;


@RequiredArgsConstructor
@Service
public class IdeaService {
    private final IdeaRepository ideaRepository;
    private final SessionRepository sessionRepository;
    private final TagRepository tagRepository;

    public IdeaResponse createIdea(User author, IdeaRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException(request.getSessionId()));

        session.getSessionUsers().stream().filter(sessionUser -> sessionUser.getId().getUuid().equals(author.getUuid()))
                .findFirst().ifPresent(sessionUser -> {
                    if(!sessionUser.isSubmitIdea()) {
                        sessionUser.updateSubmitIdea();
                    }
        });

        sessionRepository.save(session);

        List<Tag> tags = tagRepository.findAllById(request.getTags());
        return IdeaResponse.of(ideaRepository.save(Idea.of(session, author, tags, request)));
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

    public List<IdeaResponse> geIdeaListBySessionId(Integer sessionId) {
        List<Idea> ideaList = ideaRepository.findAllBySessionSessionId(sessionId);
        return ideaList.stream()
                .sorted(Comparator.comparing(Idea::getIdeaId).reversed())
                .map(idea -> {
                    IdeaResponse ideaResponse = IdeaResponse.of(idea);
                    ideaResponse.setOrderNumber(ideaList.indexOf(idea) + 1);
                    return ideaResponse;
                }).collect(Collectors.toList());
    }

    public void deleteIdea(User author, Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));

        if(!idea.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("해당 아이디어의 작성자가 아닙니다");
        }

        ideaRepository.delete(idea);
    }

    public void ideaVote(User voter, Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId).orElseThrow(() -> new IdeaNotFoundException(ideaId));

        Optional<SessionUser> sessionUserOptional = idea.getSession().getSessionUsers().stream()
                .filter(sessionUser -> sessionUser.getUser().getUuid().equals(voter.getUuid()))
                .findAny();
        if(!sessionUserOptional.isPresent()) {
            throw new NotHasRightVoteException();
        }
        if(sessionUserOptional.get().getVoteCount() == sessionUserOptional.get().getSession().getMaxVoteCount()) {
            throw new IllegalArgumentException("최대 투표 수를 모두 소모하였습니다.");
        }

        idea.vote();
        ideaRepository.save(idea);
        Session session = idea.getSession();
        session.getSessionUsers().stream()
                .filter(sessionUser -> sessionUser.getUser().getUuid().equals(voter.getUuid()))
                .findFirst()
                .ifPresent(sessionUser -> {
                    sessionUser.plusVoteCount();
                    if(!sessionUser.isVoted()) {
                        sessionUser.updateVoted();
                    }
                });

        sessionRepository.save(session);
    }
}
