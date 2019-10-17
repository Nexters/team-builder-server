package com.nexters.teambuilder.tag.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    List<Tag> findAllByTagIdIn(List<Integer> tagIds);
}
