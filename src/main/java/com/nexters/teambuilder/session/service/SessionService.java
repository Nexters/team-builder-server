package com.nexters.teambuilder.session.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.nexters.teambuilder.idea.domain.IdeaRepository;
import com.nexters.teambuilder.session.api.dto.SessionNumber;
import com.nexters.teambuilder.session.api.dto.SessionRequest;
import com.nexters.teambuilder.session.api.dto.SessionResponse;
import com.nexters.teambuilder.session.domain.Session;
import com.nexters.teambuilder.session.domain.SessionRepository;
import com.nexters.teambuilder.session.exception.SessionNotFoundException;
import com.nexters.teambuilder.tag.domain.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public Session getSession(Integer sessionNumber) {
        return sessionRepository.findBySessionNumber(sessionNumber)
                .orElseThrow(() -> new SessionNotFoundException(sessionNumber));
    }

    public Session getLatestSession() {
        return sessionRepository.findTopByOrderBySessionNumber()
                .orElseThrow(() -> new SessionNotFoundException(0));
    }

    public Session createSession(SessionRequest sessionRequest) {
        Session session = Session.of(sessionRequest);
        return sessionRepository.save(session);
    }

    public Session updateSession(Integer sessionNumber, SessionRequest sessionRequest) {
        Session session = sessionRepository.findBySessionNumber(sessionNumber)
                .orElseThrow(() -> new SessionNotFoundException(sessionNumber));

        session.update(sessionRequest);

        return sessionRepository.save(session);
    }

    public List<SessionNumber> sessionNumberList() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream()
                .sorted(Comparator.comparing(Session::getSessionNumber).reversed())
                .map(session -> new SessionNumber(session.getSessionNumber()))
                .collect(Collectors.toList());
    }
}
