package com.nexters.teambuilder.session.service;

import com.nexters.teambuilder.session.api.dto.SessionRequest;
import com.nexters.teambuilder.session.domaiin.Session;
import com.nexters.teambuilder.session.domaiin.SessionRepository;
import com.nexters.teambuilder.session.exception.SessionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public Session getSession(Integer sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new SessionNotFoundException(sessionId));
    }

    public Session getLatestSession() {
        return sessionRepository.findTopByOrderBySessionNumber()
                .orElseThrow(() -> new SessionNotFoundException(0));
    }

    public Session createSession(SessionRequest sessionRequest) {
        Session session = Session.of(sessionRequest);
        return sessionRepository.save(session);
    }
}
