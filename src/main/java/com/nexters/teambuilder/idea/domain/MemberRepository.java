package com.nexters.teambuilder.idea.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    List<Member> findAllByUuidAndIdeaId(String uuid, Integer ideaId);
    List<Member> findByIdeaId(Integer ideaId);
}
