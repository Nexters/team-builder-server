package com.nexters.teambuilder.idea.domain;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdeaRepository extends JpaRepository<Idea, Integer> {
    List<Idea> findAllBySessionSessionId(Integer sessionId);
    List<Idea> findAllByIdeaIdIn(List<Integer> ideaIds);
    boolean existsIdeaByIdeaId(Integer ideaId);
}
