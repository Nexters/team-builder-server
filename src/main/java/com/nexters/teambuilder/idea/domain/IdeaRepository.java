package com.nexters.teambuilder.idea.domain;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface IdeaRepository extends JpaRepository<Idea, Integer> {
    List<Idea> findAllBySessionSessionId(Integer sessionId);
}
