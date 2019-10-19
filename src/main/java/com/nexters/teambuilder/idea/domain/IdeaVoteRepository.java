package com.nexters.teambuilder.idea.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdeaVoteRepository extends JpaRepository<IdeaVote, Integer> {
    List<IdeaVote> findAllByUuidAndSessionNumber(String uuid, Integer sessionId);
}
