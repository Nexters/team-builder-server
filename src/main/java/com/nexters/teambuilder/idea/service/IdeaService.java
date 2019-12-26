package com.nexters.teambuilder.idea.service;

import com.nexters.teambuilder.common.exception.NotValidPeriodException;
import com.nexters.teambuilder.favorite.domain.Favorite;
import com.nexters.teambuilder.favorite.domain.FavoriteRepository;
import com.nexters.teambuilder.idea.api.dto.*;
import com.nexters.teambuilder.idea.domain.Idea;
import com.nexters.teambuilder.idea.domain.IdeaRepository;
import com.nexters.teambuilder.idea.domain.IdeaVote;
import com.nexters.teambuilder.idea.domain.IdeaVoteRepository;
import com.nexters.teambuilder.idea.exception.IdeaNotFoundException;
import com.nexters.teambuilder.idea.exception.NotHasRightVoteException;
import com.nexters.teambuilder.idea.exception.UserForbiddenActionException;
import com.nexters.teambuilder.idea.exception.UserHasTeamException;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nexters.teambuilder.user.domain.User.Role.ROLE_ADMIN;
import static com.nexters.teambuilder.user.domain.User.Role.ROLE_USER;


@RequiredArgsConstructor
@Service
public class IdeaService {
    private final IdeaRepository ideaRepository;
    private final IdeaVoteRepository ideaVoteRepository;
    private final SessionRepository sessionRepository;
    private final TagRepository tagRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public IdeaResponse createIdea(User author, IdeaRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException(request.getSessionId()));

        checkValidPeriodForAction(author, session, Period.PeriodType.IDEA_COLLECT);

        if (!author.isActivated() && author.getRole().equals(ROLE_USER)) {
            throw new UserNotActivatedException();
        }

        if (!author.isSubmitIdea()) {
            author.updateSubmitIdea(true);
            userRepository.save(author);
        }

        List<Tag> tags = tagRepository.findAllById(request.getTags());

        return IdeaResponse.of(ideaRepository.save(Idea.of(session, author, tags, request)));
    }

    public IdeaResponse getIdea(User user, Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));

        IdeaResponse ideaResponse = IdeaResponse.of(idea);

        favoriteRepository.findFavoriteByIdeaIdAndUuid(ideaId, user.getUuid())
                .ifPresent(favorite -> ideaResponse.setFavorite(true));
        return ideaResponse;
    }

    public IdeaResponse updateIdea(User author, Integer ideaId, IdeaRequest request) {

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));

        if (!idea.getAuthor().getId().equals(author.getId()) && author.getRole().equals(ROLE_USER)) {
            throw new IllegalArgumentException("해당 아이디어의 작성자가 아닙니다");
        }

        List<Tag> tags = tagRepository.findAllByTagIdIn(request.getTags());

        idea.update(request, tags);

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

                    favoriteList.forEach(favorite ->
                            addFavoriteToIdeaResponse(favorite, idea, ideaResponse));

                    return ideaResponse;
                }).collect(Collectors.toList());
    }

    private void addFavoriteToIdeaResponse(Favorite favorite, Idea idea, IdeaResponse ideaResponse) {
        if (favorite.getIdeaId().equals(idea.getIdeaId())) {
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

                    favoriteList.forEach(favorite ->
                            addFavoriteToIdeaResponse(favorite, idea, ideaResponse));

                    return ideaResponse;
                }).collect(Collectors.toList());
    }

    public void deleteIdea(User author, Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));

        if (!idea.getAuthor().getId().equals(author.getId()) && author.getRole().equals(ROLE_USER)) {
            throw new IllegalArgumentException("해당 아이디어의 작성자가 아닙니다");
        }

        favoriteRepository.findFavoriteByIdeaIdAndUuid(ideaId, author.getUuid()).ifPresent(favoriteRepository::delete);

        ideaRepository.delete(idea);
    }

    public void ideaVote(User voter, Integer ideaId) {
        Idea idea = ideaRepository.findById(ideaId).orElseThrow(() -> new IdeaNotFoundException(ideaId));

        Optional<SessionUser> sessionUserOptional = idea.getSession().getSessionUsers().stream()
                .filter(sessionUser -> sessionUser.getUser().getUuid().equals(voter.getUuid()))
                .findAny();

        checkValidPeriodForAction(voter, idea.getSession(), Period.PeriodType.IDEA_VOTE);

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

    public void ideasVote(User voter, List<Integer> ideaId) {
        if (!voter.isActivated()) {
            throw new UserForbiddenActionException();
        }

        List<Idea> ideas = ideaRepository.findAllByIdeaIdIn(ideaId);

        ideas.stream().findFirst()
                .ifPresent(idea -> checkValidPeriodForAction(voter, idea.getSession(), Period.PeriodType.IDEA_VOTE));

        ideas.stream().forEach(idea -> {
            idea.vote();
            ideaVoteRepository.save(new IdeaVote(idea.getIdeaId(), idea.getSession().getSessionNumber(), voter.getUuid()));
            ideaRepository.save(idea);
        });

        voter.updateVoteCount(ideas.size());
        if (!voter.isVoted()) {
            voter.updateVoted(true);
        }

        userRepository.save(voter);
    }

    public List<VotedIdeaResponse> votedIdeas(User user, Integer sessionNumber) {
        List<Integer> votedIdeaIds = ideaVoteRepository.findAllByUuidAndSessionNumber(user.getUuid(), sessionNumber)
                .stream().map(ideaVote -> ideaVote.getIdeaId())
                .collect(Collectors.toList());

        return ideaRepository.findAllByIdeaIdIn(votedIdeaIds).stream()
                .map(VotedIdeaResponse::of)
                .collect(Collectors.toList());
    }

    public void checkValidPeriodForAction(User user, Session session, Period.PeriodType periodType) {
        session.getPeriods().stream()
                .filter(period -> period.getPeriodType().equals(periodType)).findFirst()
                .ifPresent(period -> {
                    if (!period.isNowIn() && user.getRole().equals(ROLE_USER)) {
                        throw new NotValidPeriodException(periodType);
                    }
                });
    }

    public List<MemberResponse> addMember(User author, Integer ideaId, MemberRequest request) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNotFoundException(ideaId));

        if (!idea.getAuthor().getUuid().equals(author.getUuid()) && author.getRole().equals(ROLE_USER)) {
            throw new IllegalArgumentException("해당 아이디어의 작성자가 아닙니다");
        }

        List<User> users = userRepository.findAllByUuidIn(request.getUuids());

        if(users.stream().anyMatch(user -> user.isHasTeam() && !idea.getMembers().contains(user))) {
            List<User> hasTeamMembers = users.stream().filter(user -> user.isHasTeam() && !idea.getMembers().contains(user)).collect(Collectors.toList());
            throw new UserHasTeamException(hasTeamMembers.stream().map(MemberResponse::createMemberFrom).collect(Collectors.toList()));
        }

        List<User> newMembers = users.stream().map(newMember -> {
            newMember.updateHasTeam(true);
            return newMember;
        }).collect(Collectors.toList());

        idea.addMember(newMembers);

        ideaRepository.save(idea);

        return newMembers.stream().map(MemberResponse::createMemberFrom).collect(Collectors.toList());
    }

    public void ideaSelect(User user, List<Integer> ideaids) {
        if(!user.getRole().equals(ROLE_ADMIN)) {
            throw new UserForbiddenActionException();
        }

        List<Idea> selectedIdeas = ideaRepository.findAllByIdeaIdIn(ideaids).stream()
                .map(idea -> {
                    idea.select();
                    return idea;
                }).collect(Collectors.toList());

        ideaRepository.saveAll(selectedIdeas);
    }

    public void ideaDeselect(User user, List<Integer> ideaids) {
        if(!user.getRole().equals(ROLE_ADMIN)) {
            throw new UserForbiddenActionException();
        }

        List<Idea> selectedIdeas = ideaRepository.findAllByIdeaIdIn(ideaids).stream()
                .map(idea -> {
                    idea.deselect();
                    return idea;
                }).collect(Collectors.toList());

        ideaRepository.saveAll(selectedIdeas);
    }
}

