package com.nexters.teambuilder.session.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    /**
     * 최신 기수를 가져온다.
     * @return 최신 기수
     */
    Optional<Session> findTopByOrderBySessionNumber();

    Optional<Session> findBySessionNumber(Integer sessionNumber);
}
