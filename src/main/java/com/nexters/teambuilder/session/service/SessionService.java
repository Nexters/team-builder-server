package com.nexters.teambuilder.session.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.nexters.teambuilder.session.api.dto.SessionNumber;
import com.nexters.teambuilder.session.api.dto.SessionRequest;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.session.domain.SessionRepository;
import com.nexters.teambuilder.session.domain.SessionUser;
import com.nexters.teambuilder.session.exception.SessionNotFoundException;
import com.nexters.teambuilder.user.domain.User;
import com.nexters.teambuilder.user.domain.UserRepository;
import com.nexters.teambuilder.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public Session getSession(Integer sessionNumber) {
        return sessionRepository.findBySessionNumber(sessionNumber)
                .orElseThrow(() -> new SessionNotFoundException(sessionNumber));
    }

    public Session getLatestSession() {
        return sessionRepository.findTopByOrderBySessionNumberDesc()
                .orElseThrow(() -> new SessionNotFoundException(0));
    }

    public Session createSession(SessionRequest sessionRequest) {
        Session session = Session.of(sessionRequest);
        addSessionUserToSession(session, sessionRequest.getUsers());
        System.out.println(session.getSessionUsers().size() + "@@@");
        return sessionRepository.save(session);
    }

    public Session updateSession(Integer sessionNumber, SessionRequest sessionRequest) {
        Session session = sessionRepository.findBySessionNumber(sessionNumber)
                .orElseThrow(() -> new SessionNotFoundException(sessionNumber));

        session.update(sessionRequest);
        addSessionUserToSession(session, sessionRequest.getUsers());
        return sessionRepository.save(session);
    }

    public List<SessionNumber> sessionNumberList() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream()
                .sorted(Comparator.comparing(Session::getSessionNumber).reversed())
                .map(session -> new SessionNumber(session.getSessionNumber()))
                .collect(Collectors.toList());
    }

    private void addSessionUserToSession(Session session, List<String> uuids) {
        Set<SessionUser> sessionUsers =
                uuids.stream()
                        .map(uuid -> {
                            User user = userRepository.findUserByUuid(uuid)
                                    .orElseThrow(() -> new UserNotFoundException(uuid));
                            return new SessionUser(session, user);
                        })
                        .collect(Collectors.toSet());

        session.getSessionUsers().addAll(sessionUsers);
    }
}