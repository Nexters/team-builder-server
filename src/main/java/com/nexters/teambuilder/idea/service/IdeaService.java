package com.nexters.teambuilder.idea.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nexters.teambuilder.favorite.domain.Favorite;
import com.nexters.teambuilder.favorite.domain.FavoriteRepository;
import com.nexters.teambuilder.idea.api.dto.IdeaRequest;
import com.nexters.teambuilder.idea.api.dto.IdeaResponse;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.idea.domain.IdeaRepository;
import com.nexters.teambuilder.idea.exception.IdeaNotFoundException;
import com.nexters.teambuilder.idea.exception.NotHasRightVoteException;
import com.nexters.teambuilder.session.domain.Period;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.session.domain.SessionRepository;
import com.nexters.teambuilder.session.domain.SessionUser;
import com.nexters.teambuilder.session.exception.SessionNotFoundException;
import com.nexters.teambuilder.tag.domain.Tag;
import com.nexters.teambuilder.tag.domain.TagRepository;
import com.nexters.teambuilder.user.domain.User;
import com.nexters.teambuilder.user.domain.UserRepository;
import com.nexters.teambuilder.user.exception.UserNotActivatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class IdeaService {
    private final IdeaRepository ideaRepository;
    private final SessionRepository sessionRepository;
    private final TagRepository tagRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;


    public IdeaResponse createIdea(User author, IdeaRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException(request.getSessionId()));

        if (!author.isActivated()) {
            throw new UserNotActivatedException();
        }

        session.getPeriods().stream()
                .filter(period -> period.getPeriodType().equals(Period.PeriodType.IDEA_COLLECT))
                .map(period -> {
                    if (!period.isNowIn()) {
                        throw new IllegalArgumentException("now is not in idea collect period");
                    }
                    return null;
                });

        if(!author.isSubmitIdea()) {
            author.updateSubmitIdea(true);
            userRepository.save(author);
        }

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

        if (!idea.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("해당 아이디어의 작성자가 아닙니다");
        }

        idea.update(request);

        return IdeaResponse.of(ideaRepository.save(idea));
    }

    public List<IdeaResponse> getIdeaList(User user) {
        List<Idea> ideaList = ideaRepository.findAll();

        List<Favorite> favoriteList = favoriteRepository.findAllByUuid(user.getUuid());

        return ideaList.stream()
                .sorted(Comparator.comparing(Idea::getIdeaId).reversed())
                .map(idea -> {
                    IdeaResponse ideaResponse = IdeaResponse.of(idea);
                    ideaResponse.setOrderNumber(ideaList.indexOf(idea) + 1);

                    favoriteList.forEach(favorite->
                            addFavoriteToIdeaResponse(favorite, idea, ideaResponse));

                    return ideaResponse;
                }).collect(Collectors.toList());
    }

    private void addFavoriteToIdeaResponse(Favorite favorite, Idea idea, IdeaResponse ideaResponse) {
        if(favorite.getIdeaId().equals(idea.getIdeaId())){
            ideaResponse.setFavorite(true);
        }
    }

    public List<IdeaResponse> getIdeaListBySessionId(Integer sessionId, User user) {
        List<Idea> ideaList = ideaRepository.findAllBySessionSessionId(sessionId);

        List<Favorite> favoriteList = favoriteRepository.findAllByUuid(user.getUuid());

        return ideaList.stream()
                .sorted(Comparator.comparing(Idea::getIdeaId).reversed())
                .map(idea -> {
                    IdeaResponse ideaResponse = IdeaResponse.of(idea);
                    ideaResponse.setOrderNumber(ideaList.indexOf(idea) + 1);

                    favoriteList.forEach(favorite->
                            addFavoriteToIdeaResponse(favorite, idea, ideaResponse));

                    return ideaResponse;
                }).collect(Collectors.toList());
    }

    public void deleteIdea(User author, Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));

        if (!idea.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("해당 아이디어의 작성자가 아닙니다");
        }

        ideaRepository.delete(idea);
    }

    public void ideaVote(User voter, Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId).orElseThrow(() -> new IdeaNotFoundException(ideaId));

        Optional<SessionUser> sessionUserOptional = idea.getSession().getSessionUsers().stream()
                .filter(sessionUser -> sessionUser.getUser().getUuid().equals(voter.getUuid()))
                .findAny();
        if (!sessionUserOptional.isPresent()) {
            throw new NotHasRightVoteException();
        }
        if (sessionUserOptional.get().getVoteCount() == sessionUserOptional.get().getSession().getMaxVoteCount()) {
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
                    if (!sessionUser.isVoted()) {
                        sessionUser.updateVoted();
                    }
                });

        sessionRepository.save(session);
    }
}
